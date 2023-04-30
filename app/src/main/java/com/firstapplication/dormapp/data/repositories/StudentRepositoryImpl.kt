package com.firstapplication.dormapp.data.repositories

import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.*
import com.firstapplication.dormapp.data.remote.*
import com.firstapplication.dormapp.enums.ConfirmedRegistration
import com.firstapplication.dormapp.sealed.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepositoryImpl @Inject constructor(
    database: FirebaseDatabase,
    private val newsDao: SavedNewsDao,
    private val dataSource: RealtimeDataSource
) : StudentRepository {

    private val referenceNews = database.reference.child(PACKAGE_NEWS)
    private val referenceRegistration = database.reference.child(PACKAGE_REGISTRATION)
    private val referenceUsers = database.reference.child(PACKAGE_USERS)

    override suspend fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity): LoginStudentResult {
        val verifyEntity = dataSource.getObjectFrom<StudentVerifyEntity>(referenceUsers.child(studentVerifyEntity.passNumber.toString()))
        if (verifyEntity.result == null) return DbErrorResult
        if (verifyEntity.result == studentVerifyEntity) return CorrectLoginResult

        return checkNotRegisteredStudents(studentVerifyEntity)
    }

    override suspend fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity): DatabaseResult {
        val user = dataSource.getObjectFrom<StudentEntity>(referenceUsers.child(studentVerifyEntity.passNumber.toString()))
        if (user.error != null) return Error(R.string.error)
        if (user.result == null) return Error(R.string.user_not_found)

        if (user.result.getStudentVerifyEntity() != studentVerifyEntity) return Error(R.string.error)

        val student = dataSource.getObjectFrom<StudentEntity>(referenceRegistration.child(user.result.passNumber.toString()))
        if (student.error != null) return Error(R.string.error)
        if (student.result == null) return Correct(user.result)

        return if (student.result == user.result) Error(R.string.user_not_found)
        else Correct(user.result)
    }

    override suspend fun getNews(): SelectResult {
        val news = dataSource.readAllFrom<NewsEntity>(referenceNews) ?: return ErrorSelect
        return CorrectSelect(news)
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
        val isAdded = dataSource.setObject(referenceRegistration.child(studentEntity.passNumber.toString()), registerEntity)
        return if (isAdded.error != null) Error(R.string.error)
        else Correct<Any>()
    }

    override suspend fun checkStudentAsWorkerOf(id: String, userPass: Int): ResponseResult {
        val reference = referenceNews.child(id).child(PACKAGE_RESPONSE).child(userPass.toString())

        val isRegistered = dataSource.getObjectFrom<Int>(reference)
        if (isRegistered.error != null) return ErrorResponse
        if (isRegistered.result != null) return AlreadyRegisteredResponse

        val isAdded = dataSource.setObject(reference, userPass)
        return if (isAdded.error != null) ErrorResponse
        else CorrectResponse
    }

    private suspend fun doIfNotRegistered(
        reference: DatabaseReference,
        entity: StudentEntity,
        isRegisterReference: Boolean = false
    ): DatabaseResult {

        val user = dataSource.getObjectFrom<StudentRegistrationEntity>(reference.child(entity.passNumber.toString()))
        if (user.error != null) return Error(R.string.error)

        return if (user.result == null) {
            Correct<Any>()
        } else if (isRegisterReference && user.result.confirmed == ConfirmedRegistration.NOT_CONFIRMED.value) {
            Correct<Any>()
        } else {
            Error(R.string.already_registered)
        }
    }

    private suspend fun checkNotRegisteredStudents(entity: StudentVerifyEntity): LoginStudentResult {
        val user = dataSource.getObjectFrom<StudentRegistrationEntity>(referenceRegistration.child(entity.passNumber.toString()))
        if (user.error != null) return DbErrorResult
        if (user.result == null) return NotFoundResult

        return if (user.result.getStudentVerifyEntity() == entity) {
            if (user.result.confirmed == ConfirmedRegistration.NOT_CONFIRMED.value) DeletedLoginResult else NotVerifiedResult
        } else {
            NotFoundResult
        }
    }
}