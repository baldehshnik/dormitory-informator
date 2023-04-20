package com.firstapplication.dormapp.data.repositories

import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.*
import com.firstapplication.dormapp.data.remote.*
import com.firstapplication.dormapp.enums.ConfirmedRegistration
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.utils.getOnce
import com.firstapplication.dormapp.utils.logRealtimeError
import com.firstapplication.dormapp.utils.set
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
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

    override suspend fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity): LoginStudentResult {
        var result: LoginStudentResult? = null
        referenceUsers.child(studentVerifyEntity.passNumber.toString())
            .getOnce()
            .onDataChange { snapshot ->
                if (snapshot.dataSnapshot.exists()) {
                    val user = snapshot.dataSnapshot.getValue<StudentVerifyEntity>()
                    if (user == studentVerifyEntity) result = CorrectLoginResult
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = DbErrorResult
            }.read()

        if (result == DbErrorResult || result == CorrectLoginResult) return result!!
        result = checkNotRegisteredStudents(studentVerifyEntity)

        return result ?: DbErrorResult
    }

    override suspend fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity): DatabaseResult {
        var result: DatabaseResult? = null
        var student: StudentEntity? = null
        referenceUsers.child(studentVerifyEntity.passNumber.toString())
            .getOnce()
            .onDataChange { snapshot ->
                if (snapshot.dataSnapshot.exists()) {
                    val user = snapshot.dataSnapshot.getValue<StudentVerifyEntity>()
                    if (user != studentVerifyEntity) {
                        result = Error(R.string.user_not_found)
                    } else {
                        student = snapshot.dataSnapshot.getValue<StudentEntity>()
                        if (student == null) result = Error(R.string.user_not_found)
                    }
                } else {
                    result = Error(R.string.user_not_found)
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = Error(R.string.error)
            }.read()

        if (result is Error) return result!!
        else if (student == null) return Error(R.string.error)

        referenceRegistration.child(student!!.passNumber.toString())
            .getOnce()
            .onDataChange { snapshot ->
                result = if (snapshot.dataSnapshot.exists()) {
                    val value = snapshot.dataSnapshot.getValue<StudentEntity>()
                    if (value == student) Error(R.string.user_not_found)
                    else Correct(student)
                } else {
                    Correct(student)
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = Error(R.string.database_error)
            }.read()

        return result ?: Error(R.string.error)
    }

    override suspend fun getNews(): SelectResult {
        var result: SelectResult? = null
        val news = mutableListOf<NewsEntity>()
        referenceNews.getOnce()
            .childrenDataChange(true)
            .onDataChange { snapshot ->
                val entity = snapshot.dataSnapshot.getValue<NewsEntity>()
                if (entity != null) news.add(entity)
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = ErrorSelect
            }.read()

        if (result != ErrorSelect) result = CorrectSelect(news)
        return result ?: ErrorSelect
    }

    override suspend fun readSavedNewsFromLocalDB(): List<SavedNewsEntity> {
        return newsDao.readAllSavedNews()
    }

    override suspend fun registerStudent(studentEntity: StudentEntity): DatabaseResult {
        var result: DatabaseResult = doIfNotRegistered(referenceUsers, studentEntity)
        if (result is Error) return result

        result = doIfNotRegistered(referenceRegistration, studentEntity, true)
        if (result is Error) return result

        val registerEntity = StudentRegistrationEntity(studentEntity)
        referenceRegistration.child(studentEntity.passNumber.toString())
            .set(registerEntity) { error, _ ->
                result = if (error != null) {
                    logRealtimeError(this, error)
                    Error(R.string.error)
                } else {
                    Correct<Any>()
                }
            }

        return result
    }

    override suspend fun checkStudentAsWorkerOf(id: String, userPass: Int): ResponseResult {
        var result: ResponseResult? = null
        val reference = referenceNews.child(id).child(PACKAGE_RESPONSE).child(userPass.toString())
        reference.getOnce()
            .onDataChange { snapshot ->
                if (snapshot.dataSnapshot.exists()) result = AlreadyRegisteredResponse
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = ErrorResponse
            }.read()

        if (result == ErrorResponse || result == AlreadyRegisteredResponse) return result!!
        reference.set(userPass) { error, _ ->
            result = if (error != null) {
                logRealtimeError(this, error)
                ErrorResponse
            } else {
                CorrectResponse
            }
        }

        return result ?: ErrorResponse
    }

    private suspend fun doIfNotRegistered(
        reference: DatabaseReference,
        entity: StudentEntity,
        isRegisterReference: Boolean = false
    ): DatabaseResult {
        var result: DatabaseResult? = null
        reference.child(entity.passNumber.toString()).getOnce()
            .onDataChange { snapshot ->
                val value = snapshot.dataSnapshot.getValue<StudentEntity>()
                if (value != null && value.passNumber == entity.passNumber) {
                    result = if (
                        isRegisterReference && snapshot.dataSnapshot.child(CONFIRM)
                            .getValue<Int>() == ConfirmedRegistration.NOT_CONFIRMED.value
                    ) Correct<Any>() else Error(R.string.already_registered)

                    snapshot.cancel = true
                } else {
                    result = Correct<Any>()
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = Error(R.string.error)
            }.read()

        return result ?: Error(R.string.database_error)
    }

    private suspend fun checkNotRegisteredStudents(entity: StudentVerifyEntity): LoginStudentResult {
        var result: LoginStudentResult? = null
        referenceRegistration.child(entity.passNumber.toString())
            .getOnce()
            .onDataChange { snapshot ->
                if (!snapshot.dataSnapshot.exists()) {
                    result = NotFoundResult
                    return@onDataChange
                }

                val user = snapshot.dataSnapshot.getValue<StudentVerifyEntity>()
                result = if (user == entity) {
                    val confirm = snapshot.dataSnapshot.child(CONFIRM).getValue<Int>()
                    if (confirm == ConfirmedRegistration.NOT_CONFIRMED.value) DeletedLoginResult else NotVerifiedResult
                } else {
                    NotFoundResult
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = DbErrorResult
            }.read()

        return result ?: DbErrorResult
    }
}