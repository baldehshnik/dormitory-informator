package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.*
import com.firstapplication.dormapp.data.remote.*
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
    database: FirebaseDatabase,
    private val newsDao: SavedNewsDao
) : StudentRepository {

    private val referenceNews = database.reference.child(PACKAGE_NEWS)
    private val referenceRegistration = database.reference.child(PACKAGE_REGISTRATION)
    private val referenceUsers = database.reference.child(PACKAGE_USERS)

    private val _verifiedUser = MutableStateFlow<LoginStudentResult>(ProgressLoginResult)
    val verifiedUser: StateFlow<LoginStudentResult> get() = _verifiedUser.asStateFlow()

    private val _userDataAccount = MutableStateFlow<DatabaseResult>(Progress)
    val userDataAccount: StateFlow<DatabaseResult> get() = _userDataAccount.asStateFlow()

    private val _newsData = MutableStateFlow<SelectResult>(ProgressSelect)
    val newsData: StateFlow<SelectResult> get() = _newsData.asStateFlow()

    private val _registerResponse = MutableStateFlow<DatabaseResult>(Progress)
    val registerResponse: StateFlow<DatabaseResult> get() = _registerResponse.asStateFlow()

    private val _responseResult = MutableStateFlow<ResponseResult>(ProgressResponse)
    val responseResult: StateFlow<ResponseResult> get() = _responseResult.asStateFlow()

    override suspend fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity) {
        _verifiedUser.value = ProgressLoginResult
        val reference = referenceUsers.child(studentVerifyEntity.passNumber.toString())
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

    override suspend fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity) {
        val reference = referenceUsers.child(studentVerifyEntity.passNumber.toString())
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue<StudentVerifyEntity>()
                    if (user != studentVerifyEntity) {
                        _userDataAccount.value = Error(R.string.user_not_found)
                    } else {
                        val studentEntity = dataSnapshot.getValue<StudentEntity>()
                        if (studentEntity == null) {
                            _userDataAccount.value = Error(R.string.user_not_found)
                        } else {
                            Log.i("TOTOT", studentEntity.toString())
                            _userDataAccount.value = Correct(studentEntity)
                        }
                    }
                } else {
                    _userDataAccount.value = Error(R.string.user_not_found)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _userDataAccount.value = Error(R.string.error)
            }
        })
    }

    override suspend fun getNews() {
        referenceNews.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(allSnapshots: DataSnapshot) {
                val newsList = mutableListOf<NewsEntity>()
                for (snapshot in allSnapshots.children) {
                    val entity = snapshot.getValue<NewsEntity>()
                    if (entity != null) newsList.add(entity)
                }
                _newsData.value = CorrectSelect(newsList)
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
        doIfNotRegistered(referenceUsers, studentEntity) {
            checkWaitForRegistrationStudents(studentEntity)
        }
    }

    override suspend fun checkStudentAsWorkerOf(id: String, userPass: Int) {
        val reference = referenceNews.child(id).child(PACKAGE_RESPONSE).child(userPass.toString())
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _responseResult.value = AlreadyRegisteredResponse
                } else {
                    reference.setValue(userPass)
                    _responseResult.value = CorrectResponse
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(StudentRepositoryImpl::class.java.simpleName, error.message)
                _responseResult.value = ErrorResponse
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
        doIfNotRegistered(referenceRegistration, studentEntity) {
            val reference = referenceRegistration.child(studentEntity.passNumber.toString())
            reference.setValue(studentEntity)
            reference.child(CONFIRM).setValue(ConfirmedRegistration.EMPTY.value)

            _registerResponse.value = Correct<Any?>()
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
        val reference = referenceRegistration.child(entity.passNumber.toString())
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
            val confirm = snapshot.child(CONFIRM).getValue<Int>()
            if (confirm == ConfirmedRegistration.NOT_CONFIRMED.value) {
                _verifiedUser.value = DeletedLoginResult
                referenceRegistration.child(entity.passNumber.toString()).removeValue()
            } else {
                _verifiedUser.value = NotVerifiedResult
            }
        }
    }
}