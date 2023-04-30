package com.firstapplication.dormapp.data.repositories

import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.models.StudentRegistrationEntity
import com.firstapplication.dormapp.data.models.TimeEntity
import com.firstapplication.dormapp.data.remote.*
import com.firstapplication.dormapp.enums.ConfirmedRegistration
import com.firstapplication.dormapp.sealed.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    database: FirebaseDatabase,
    private val dataSource: RealtimeDataSource
) : AdminRepository {

    private val referenceNews = database.reference.child(PACKAGE_NEWS)
    private val referenceRegistration = database.reference.child(PACKAGE_REGISTRATION)
    private val referenceUsers = database.reference.child(PACKAGE_USERS)

    override suspend fun readNewsFromDB(): SelectResult {
        val result = dataSource.readAllFrom<NewsEntity>(referenceNews)
        return if (result == null) ErrorSelect
        else CorrectSelect(result)
    }

    override suspend fun addNews(news: NewsEntity): DatabaseResult {
        news.id = referenceNews.push().key.toString()
        return changeNews(news)
    }

    override suspend fun editNews(news: NewsEntity): DatabaseResult {
        return changeNews(news)
    }

    override suspend fun deleteNews(id: String): DatabaseResult {
        val result = dataSource.removeObject(referenceNews.child(id))
        return if (result.result == true) Correct<Any>() else Error(R.string.error)
    }

    override suspend fun readRespondedStudents(newsId: String): SelectResult {
        val data = dataSource.readAllFrom<Int>(referenceNews.child(newsId).child(PACKAGE_RESPONSE))
        return if (data == null) ErrorSelect else readRespondingStudentsByIds(data)
    }

    override suspend fun readNotRegisteredStudents(): SelectResult {
        val result = dataSource.readAllFrom<StudentRegistrationEntity>(referenceRegistration)
        return if (result != null) {
            val students = mutableListOf<StudentEntity>()
            result.forEach {
                if (it.confirmed == ConfirmedRegistration.EMPTY.value) {
                    students.add(it.toStudentEntity())
                }
            }
            CorrectSelect(students)
        } else {
            ErrorSelect
        }
    }

    override suspend fun confirmStudentRegistration(entity: StudentEntity): DatabaseResult {
        val pass = entity.passNumber.toString()

        if (dataSource.setObject(
                referenceUsers.child(pass),
                entity
            ).error != null
        ) return Error(R.string.error)

        if (dataSource.removeObject(referenceRegistration.child(pass)).result == true) return Correct<Any>()

        return if (doWhileNotTrue(referenceUsers.child(pass), Any())) Correct<Any>()
        else Error(R.string.error)
    }

    override suspend fun cancelStudentRegistration(pass: String): DatabaseResult {
        val result = dataSource.setObject(
            referenceRegistration.child(pass).child(CONFIRM),
            ConfirmedRegistration.NOT_CONFIRMED.value
        )

        return if (result.result == true) Correct<Any>() else Error(R.string.error)
    }

    override suspend fun confirmRespondedStudent(
        newsId: String,
        studentEntity: StudentEntity
    ): DatabaseResult {
        val answer = dataSource.getObjectFrom<TimeEntity>(referenceNews.child(newsId))
        if (
            answer.error != null || answer.result == null ||
            answer.result.hours == null || answer.result.timeType == null
        ) return Error(R.string.error)

        val timeEntity: TimeEntity = answer.result
        if (dataSource.removeObject(
                referenceNews.child(newsId)
                    .child(PACKAGE_RESPONSE)
                    .child(studentEntity.passNumber.toString())
            ).error != null
        ) {
            return Error(R.string.error)
        }

        if (addTimeToStudent(timeEntity.hours!!, timeEntity.timeType!!, studentEntity) is Correct<*>) {
            return Correct<Any>()
        }

        val result = doWhileNotTrue(
            referenceNews.child(newsId)
                .child(PACKAGE_RESPONSE)
                .child(studentEntity.passNumber.toString()), studentEntity.passNumber
        )

        return if (result) Correct<Any>() else Error(R.string.error)
    }

    private suspend fun addTimeToStudent(
        time: Double,
        timeType: String,
        student: StudentEntity
    ): DatabaseResult {
        when {
            timeType.startsWith("ч") || timeType.startsWith("h") -> {
                student.hours += time
            }
            timeType.startsWith("м") || timeType.startsWith("m") -> {
                student.hours += time / 60
            }
        }

        val result = dataSource.setObject(
            referenceUsers.child(student.passNumber.toString()).child(HOURS),
            student.hours
        )

        return if (result.result == true) Correct<Any>() else Error(R.string.error)
    }

    private suspend fun doWhileNotTrue(
        reference: DatabaseReference,
        value: Any
    ): Boolean {
        val startTime = Calendar.getInstance().time.time
        var result: Boolean? = null
        while (result == null) {
            val answer = dataSource.setObject(reference, value)
            if (answer.result == true) result = true
            else if (Calendar.getInstance().time.time - startTime > 2500) result = false
        }

        return result
    }

    override suspend fun cancelRespondedStudent(newsId: String, pass: String): DatabaseResult {
        val result = dataSource.removeObject(referenceNews.child(newsId).child(PACKAGE_RESPONSE).child(pass))
        return if (result.result == true) Correct<Any>() else Error(R.string.error)
    }

    private suspend fun readRespondingStudentsByIds(ids: List<Int>): SelectResult {
        val studentsList = mutableListOf<StudentEntity>()
        if (ids.isEmpty()) return CorrectSelect(studentsList)

        for (id in ids) {
            val value = dataSource.getObjectFrom<StudentEntity>(referenceUsers.child(id.toString()))
            if (value.result != null) {
                studentsList.add(value.result)
            } else if (value.error != null) {
                return ErrorSelect
            }
        }

        return CorrectSelect(studentsList)
    }

    private suspend fun changeNews(news: NewsEntity): DatabaseResult {
        val result = dataSource.setObject(referenceNews.child(news.id), news)
        return if (result.result == true) Correct<Any>() else Error(R.string.error)
    }
}