package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.*
import com.firstapplication.dormapp.enums.ConfirmedRegistration
import com.firstapplication.dormapp.sealed.ChangeResponse
import com.firstapplication.dormapp.sealed.CorrectSelect
import com.firstapplication.dormapp.sealed.ErrorSelect
import com.firstapplication.dormapp.sealed.ProgressSelect
import com.firstapplication.dormapp.data.remote.*
import com.firstapplication.dormapp.sealed.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val newsDao: SavedNewsDao
) : StudentRepository {

    private val _verifiedUser = MutableStateFlow(SingleEvent(value = 0))
    val verifiedUser: StateFlow<SingleEvent<Int>> get() = _verifiedUser.asStateFlow()

    private val _userDataAccount = MutableStateFlow(SingleEvent(StudentEntity()))
    val userDataAccount: StateFlow<SingleEvent<StudentEntity>> get() = _userDataAccount.asStateFlow()

    private val _newsData = MutableStateFlow(listOf(NewsEntity()))
    val newsData: StateFlow<List<NewsEntity>> get() = _newsData.asStateFlow()

    private val _registerResponse = MutableStateFlow<ChangeResponse>(Progress)
    val registerResponse: StateFlow<ChangeResponse> get() = _registerResponse.asStateFlow()

    private val _responseResult = MutableStateFlow<SingleEvent<ResponseResult>>(SingleEvent(ProgressResponse))
    val responseResult: StateFlow<SingleEvent<ResponseResult>> get() = _responseResult.asStateFlow()

//    override fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity) {
//        val rootReference = database.reference
//        val userReference =
//            rootReference.child(PACKAGE_USERS).child(studentVerifyEntity.passNumber.toString())
//
//        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    val passNumber = dataSnapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
//                    val roomNumber = dataSnapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
//                    val password =
//                        dataSnapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""
//>>>>>>> admin

    override fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity) {
        val reference = database.reference
            .child(PACKAGE_STUDENTS)
            .child(studentVerifyEntity.passNumber.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = getReadStudentVerifyEntity(snapshot)
                    if (user != studentVerifyEntity) checkNotRegisteredStudents(studentVerifyEntity)
                    else _verifiedUser.value = SingleEvent(value = 1)
                } else {
                    checkNotRegisteredStudents(studentVerifyEntity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _verifiedUser.value = SingleEvent(value = -2)
            }
        })
    }

    override fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity) {
        val rootReference = database.reference
        val userReference =
            rootReference.child(PACKAGE_STUDENTS).child(studentVerifyEntity.passNumber.toString())

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val passNumber = dataSnapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
                    val roomNumber = dataSnapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
                    val password = dataSnapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""

                    val user = StudentVerifyEntity(passNumber, roomNumber, password)
                    if (user != studentVerifyEntity) {
                        _userDataAccount.value = SingleEvent(StudentEntity(passNumber = 0))
                    } else {
                        val studentEntity = StudentEntity(
                            passNumber = user.passNumber,
                            roomNumber = user.roomNumber,
                            password = password
                        )

                        val fullName = dataSnapshot.child(FULL_NAME_KEY).getValue(String::class.java)
                        val hours = dataSnapshot.child(HOURS_KEY).getValue(Double::class.java)

                        if (fullName != null) studentEntity.fullName = fullName
                        if (hours != null) studentEntity.hours = hours

                        _userDataAccount.value = SingleEvent(studentEntity)
                    }
                } else {
                    _userDataAccount.value = SingleEvent(StudentEntity(passNumber = 0))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _userDataAccount.value = SingleEvent(StudentEntity(passNumber = -2))
            }

        })
    }

    override fun getNews() {
        val rootReference = database.reference
        val newsReference = rootReference.child(PACKAGE_NEWS)

        newsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(allSnapshots: DataSnapshot) {
                val newsList = mutableListOf<NewsEntity>()
                for (snapshot in allSnapshots.children) {
                    val entity = snapshot.getValue(NewsEntity::class.java)
                    if (entity != null) newsList.add(entity)
                }
                _newsData.value = newsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
            }
        })
    }

    override suspend fun readSavedNewsFromLocalDB(): List<SavedNewsEntity> {
        return newsDao.readAllSavedNews()
    }

    override suspend fun registerStudent(studentEntity: StudentEntity) {
        _registerResponse.value = Progress
        val reference = database.reference.child(PACKAGE_STUDENTS)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                val res = checkRegisterStudents(snapshots, studentEntity.passNumber)
                if (!res) {
                    checkWaitForRegistrationStudents(studentEntity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                setErrorRegisterResponse(error.message)
            }
        })
    }

    private fun checkWaitForRegistrationStudents(studentEntity: StudentEntity) {
        val reference = database.reference.child(PACKAGE_REGISTER)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                val res = checkRegisterStudents(snapshots, studentEntity.passNumber)
                if (!res) {
                    database.reference.child(PACKAGE_REGISTER)
                        .child(studentEntity.passNumber.toString())
                        .setValue(studentEntity)

                    database.reference.child(PACKAGE_REGISTER)
                        .child(studentEntity.passNumber.toString())
                        .child(CONFIRMED)
                        .setValue(ConfirmedRegistration.EMPTY.value)

                    _registerResponse.value = Correct
                }
            }

            override fun onCancelled(error: DatabaseError) {
                setErrorRegisterResponse(error.message)
            }
        })
    }

    private fun checkRegisterStudents(snapshots: DataSnapshot, passNumber: Int): Boolean {
        for (s in snapshots.children) {
            val value = s.getValue(StudentEntity::class.java)
            if (value != null && value.passNumber == passNumber) {
                _registerResponse.value = Error(R.string.already_registered)
                return true
            }
        }

        return false
    }

    private fun setErrorRegisterResponse(message: String) {
        Log.e(StudentRepositoryImpl::class.java.simpleName, message)
        _registerResponse.value = Error(R.string.database_error)
    }

    private fun checkNotRegisteredStudents(entity: StudentVerifyEntity) {
        val reference = database.reference
            .child(PACKAGE_REGISTER)
            .child(entity.passNumber.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = getReadStudentVerifyEntity(snapshot)
                    if (user != entity) _verifiedUser.value = SingleEvent(value = -1)
                    else _verifiedUser.value = SingleEvent(value = 2)
                } else {
                    _verifiedUser.value = SingleEvent(value = -1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _verifiedUser.value = SingleEvent(value = -2)
            }
        })
    }

    override suspend fun checkStudentAsWorkerOf(id: String, userPass: Int) {
        val reference = database.reference
            .child(PACKAGE_NEWS)
            .child(id)
            .child(PACKAGE_RESPONSE)
            .child(userPass.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _responseResult.value = SingleEvent(AlreadyRegisteredResponse)
                } else {
                    reference.setValue(userPass)
                    _responseResult.value = SingleEvent(CorrectResponse)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _responseResult.value = SingleEvent(ErrorResponse)
            }
        })
    }

    private fun getReadStudentVerifyEntity(snapshot: DataSnapshot): StudentVerifyEntity {
        val passNumber = snapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
        val roomNumber = snapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
        val password = snapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""
        return StudentVerifyEntity(passNumber, roomNumber, password)
    }

    companion object {
        const val PACKAGE_STUDENTS = "students"
        const val PACKAGE_NEWS = "news"
        const val PACKAGE_REGISTER = "register"

        const val PASS_KEY = "passNumber"
        const val ROOM_KEY = "roomNumber"
        const val PASSWORD_KEY = "password"
        const val FULL_NAME_KEY = "fullName"
        const val HOURS_KEY = "hours"
        const val CONFIRMED = "confirmed"
    }
}