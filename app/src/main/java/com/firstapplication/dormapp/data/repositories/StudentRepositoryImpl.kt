package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.*
import com.firstapplication.dormapp.data.remote.PACKAGE_RESPONSE
import com.firstapplication.dormapp.enums.ConfirmedRegistration
import com.firstapplication.dormapp.sealed.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
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

    private val _verifiedUser = MutableStateFlow<LoginStudentResult>(ProgressLoginResult)
    val verifiedUser: StateFlow<LoginStudentResult> get() = _verifiedUser.asStateFlow()

    private val _userDataAccount = MutableStateFlow(SingleEvent(StudentEntity()))
    val userDataAccount: StateFlow<SingleEvent<StudentEntity>> get() = _userDataAccount.asStateFlow()

    private val _newsData = MutableStateFlow(listOf(NewsEntity()))
    val newsData: StateFlow<List<NewsEntity>> get() = _newsData.asStateFlow()

    private val _registerResponse = MutableStateFlow<ChangeResponse>(Progress)
    val registerResponse: StateFlow<ChangeResponse> get() = _registerResponse.asStateFlow()

    private val _responseResult =
        MutableStateFlow<SingleEvent<ResponseResult>>(SingleEvent(ProgressResponse))
    val responseResult: StateFlow<SingleEvent<ResponseResult>> get() = _responseResult.asStateFlow()

    override fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity) {
        _verifiedUser.value = ProgressLoginResult
        val reference = database.reference
            .child(PACKAGE_STUDENTS)
            .child(studentVerifyEntity.passNumber.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<StudentVerifyEntity>()
                    if (user != studentVerifyEntity) checkNotRegisteredStudents(studentVerifyEntity)
                    else _verifiedUser.value = CorrectLoginResult
                } else {
                    checkNotRegisteredStudents(studentVerifyEntity)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _verifiedUser.value = DbErrorResult
            }
        })
    }

    override fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity) {
        val reference = database.reference.child(PACKAGE_STUDENTS)
            .child(studentVerifyEntity.passNumber.toString())

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<StudentVerifyEntity>()
                    if (user != studentVerifyEntity) {
                        _userDataAccount.value = SingleEvent(StudentEntity(passNumber = 0))
                    } else {
                        val studentEntity = dataSnapshot.getValue<StudentEntity>()
                        if (studentEntity == null) {
                            _userDataAccount.value = SingleEvent(StudentEntity(passNumber = 0))
                        } else {
                            _userDataAccount.value = SingleEvent(studentEntity)
                        }
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
        database.reference.child(PACKAGE_NEWS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(allSnapshots: DataSnapshot) {
                val newsList = mutableListOf<NewsEntity>()
                for (snapshot in allSnapshots.children) {
                    val entity = snapshot.getValue<NewsEntity>()
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
        doIfNotRegistered(reference, studentEntity) {
            checkWaitForRegistrationStudents(studentEntity)
        }
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

    private fun doIfNotRegistered(
        reference: DatabaseReference,
        entity: StudentEntity,
        doWork: () -> Unit
    ) = reference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val res = checkRegisterStudents(snapshot, entity.passNumber)
            if (!res) doWork()
        }

        override fun onCancelled(error: DatabaseError) {
            setErrorRegisterResponse(error.message)
        }
    })

    private fun checkWaitForRegistrationStudents(studentEntity: StudentEntity) {
        val reference = database.reference.child(PACKAGE_REGISTER)
        doIfNotRegistered(reference, studentEntity) {
            database.reference.child(PACKAGE_REGISTER)
                .child(studentEntity.passNumber.toString()).apply {
                    setValue(studentEntity)
                    child(CONFIRMED).setValue(ConfirmedRegistration.EMPTY.value)
                }

            _registerResponse.value = Correct
        }
    }

    private fun checkRegisterStudents(snapshots: DataSnapshot, passNumber: Int): Boolean {
        for (s in snapshots.children) {
            val value = s.getValue<StudentEntity>()
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
                    handleNotRegisteredStudent(snapshot, entity)
                } else {
                    _verifiedUser.value = NotFoundResult
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _verifiedUser.value = DbErrorResult
            }
        })
    }

    private fun handleNotRegisteredStudent(snapshot: DataSnapshot, entity: StudentVerifyEntity) {
        val user = snapshot.getValue<StudentVerifyEntity>()
        if (user != entity) {
            _verifiedUser.value = NotFoundResult
        } else {
            val confirm = snapshot.child(CONFIRMED).getValue<Int>()
            if (confirm == ConfirmedRegistration.NOT_CONFIRMED.value) {
                _verifiedUser.value = DeletedLoginResult
                database.reference.child(PACKAGE_REGISTER)
                    .child(entity.passNumber.toString())
                    .removeValue()
            } else {
                _verifiedUser.value = NotVerifiedResult
            }
        }
    }

    companion object {
        const val PACKAGE_STUDENTS = "students"
        const val PACKAGE_NEWS = "news"
        const val PACKAGE_REGISTER = "register"

        const val CONFIRMED = "confirmed"
    }
}