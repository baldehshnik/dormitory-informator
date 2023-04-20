package com.firstapplication.dormapp.data.repositories

import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.remote.*
import com.firstapplication.dormapp.enums.ConfirmedRegistration
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.utils.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepositoryImpl @Inject constructor(
    database: FirebaseDatabase
) : AdminRepository {

    private val referenceNews = database.reference.child(PACKAGE_NEWS)
    private val referenceRegistration = database.reference.child(PACKAGE_REGISTRATION)
    private val referenceUsers = database.reference.child(PACKAGE_USERS)

    // migrated
    override suspend fun readNewsFromDB(): SelectResult {
        val newsList = mutableListOf<NewsEntity>()
        var result: SelectResult? = null
        referenceNews.getOnce()
            .childrenDataChange(true)
            .onDataChange { snapshot ->
                val entity = snapshot.dataSnapshot.getValue<NewsEntity>()
                if (entity != null) newsList.add(entity)
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = ErrorSelect
            }.read()

        return result ?: CorrectSelect(newsList)
    }

    // migrated
    override suspend fun addNews(news: NewsEntity): DatabaseResult {
        news.id = referenceNews.push().key.toString()
        return changeNews(news)
    }

    // migrated
    override suspend fun editNews(news: NewsEntity): DatabaseResult {
        return changeNews(news)
    }

    // migrated
    override suspend fun deleteNews(id: String): DatabaseResult {
        var result: DatabaseResult? = null
        referenceNews.child(id).remove { error, _ ->
            if (error != null) logRealtimeError(this, error)
            else result = Correct<Any>()
        }

        return result ?: Error(R.string.error)
    }

    // migrated
    override suspend fun readRespondedStudents(newsId: String): SelectResult {
        var result: SelectResult? = null
        val ids = mutableListOf<Int>()
        referenceNews.child(newsId).child(PACKAGE_RESPONSE)
            .getOnce()
            .childrenDataChange(true)
            .onDataChange { snapshot ->
                val entity = snapshot.dataSnapshot.getValue<Int>()
                if (entity != null) ids.add(entity)
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = ErrorSelect
            }.read()

        return if (result == ErrorSelect) result!! else readRespondingStudentsByIds(ids)
    }

    // migrated
    override suspend fun readNotRegisteredStudents(): SelectResult {
        val students = mutableListOf<StudentEntity>()
        var result: SelectResult? = null
        referenceRegistration.getOnce()
            .childrenDataChange(true)
            .onDataChange { snapshot ->
                val student = snapshot.dataSnapshot.getValue<StudentEntity>()
                val confirmation = snapshot.dataSnapshot.child(CONFIRM).getValue<Int>()
                if (student != null && confirmation == ConfirmedRegistration.EMPTY.value) {
                    students.add(student)
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = ErrorSelect
            }.read()

        return result ?: CorrectSelect(students)
    }

    // migrated
    override suspend fun confirmStudentRegistration(entity: StudentEntity): DatabaseResult {
        var result: DatabaseResult? = null
        val pass = entity.passNumber.toString()
        referenceUsers.child(pass).set(entity) { error, _ ->
            if (error != null) result = Error(R.string.error)
        }

        if (result is Error) return result!!
        referenceRegistration.child(pass).remove { error, _ ->
            if (error == null) result = Correct<Any>()
        }

        if (result is Error) removeWhileNotTrue(referenceUsers.child(pass))
        return result ?: Error(R.string.error)
    }

    // migrated
    private suspend fun removeWhileNotTrue(reference: DatabaseReference) {
        val startTime = Calendar.getInstance().time.time
        var result: Boolean? = null
        while (result == null) {
            reference.remove { error, _ ->
                if (error == null) result = true
                else if (Calendar.getInstance().time.time - startTime > 2500) result = false
            }
        }
    }

    // migrated
    override suspend fun cancelStudentRegistration(pass: String): DatabaseResult {
        var result: DatabaseResult? = null
        referenceRegistration.child(pass).child(CONFIRM)
            .set(ConfirmedRegistration.NOT_CONFIRMED.value) { error, _ ->
                if (error == null) result = Correct<Any>()
                else logRealtimeError(this, error)
            }

        return result ?: Error(R.string.error)
    }

    // worked
    override suspend fun confirmRespondedStudent(
        newsId: String,
        studentEntity: StudentEntity
    ): DatabaseResult {
        var result: DatabaseResult? = null
        var time: Double? = null
        var timeType: String? = null

        referenceNews.child(newsId)
            .getOnce()
            .onDataChange { snapshot ->
                time = snapshot.dataSnapshot.child(HOURS).getValue<Double>()
                timeType = snapshot.dataSnapshot.child(TIME_TYPE).getValue<String>()
                if (time == null || timeType == null) {
                    result = Error(R.string.error)
                }
            }
            .onCancelled { error ->
                logRealtimeError(this, error)
                result = Error(R.string.error)
            }.read()

        if (result is Error) return result!!
        result = remove(referenceNews.child(newsId)
            .child(PACKAGE_RESPONSE)
            .child(studentEntity.passNumber.toString())
        )

        if (result is Error) return result!!
        result = addTimeToStudent(time!!, timeType!!, studentEntity)

        result = Error(R.string.error)
        if (result is Error) {
            removeTimeToStudent(time!!, timeType!!, studentEntity)
            return result!!
        }
        return result ?: Error(R.string.error)
    }

    // worked
    private suspend fun remove(reference: DatabaseReference): DatabaseResult {
        var result: DatabaseResult? = null
        reference.remove { error, _ ->
            if (error != null) logRealtimeError(this, error)
            else result = Correct<Any>()
        }

        return result ?: Error(R.string.error)
    }

    // worked
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

        var result: DatabaseResult? = null
        referenceUsers.child(student.passNumber.toString()).child(HOURS)
            .set(student.hours) { error, _ ->
                if (error == null) result = Correct<Any>()
                else logRealtimeError(this, error)
            }

        return result ?: Error(R.string.error)
    }

    // worked
    private suspend fun removeTimeToStudent(time: Double, timeType: String, student: StudentEntity) {
        when {
            timeType.startsWith("ч") || timeType.startsWith("h") -> {
                student.hours -= time
            }
            timeType.startsWith("м") || timeType.startsWith("m") -> {
                student.hours -= time / 60
            }
        }

        setWhileNotTrue(referenceUsers.child(student.passNumber.toString()).child(HOURS), student.hours)
    }

    // worked
    private suspend fun setWhileNotTrue(reference: DatabaseReference, value: Any) {
        val startTime = Calendar.getInstance().time.time
        var result: Boolean? = null
        while (result == null) {
            reference.set(value) { error, _ ->
                if (error == null) result = true
                else if (Calendar.getInstance().time.time - startTime > 2500) result = false
            }
        }
    }

    // migrated
    override suspend fun cancelRespondedStudent(newsId: String, pass: String): DatabaseResult {
        var result: DatabaseResult? = null
        referenceNews.child(newsId).child(PACKAGE_RESPONSE).child(pass)
            .remove { error, _ ->
                if (error == null) result = Correct<Any>()
                else logRealtimeError(this, error)
            }

        return result ?: Error(R.string.error)
    }

    // migrated
    private suspend fun readRespondingStudentsByIds(ids: List<Int>): SelectResult {
        val studentsList = mutableListOf<StudentEntity>()
        if (ids.isEmpty()) return CorrectSelect(studentsList)

        var isError = false
        for (id in ids) {
            if (isError) return ErrorSelect
            referenceUsers.child(id.toString())
                .getOnce()
                .onDataChange { snapshot ->
                    studentsList.add(snapshot.dataSnapshot.getValue<StudentEntity>() ?: return@onDataChange)
                }
                .onCancelled { error ->
                    logRealtimeError(this, error)
                    isError = true
                }.read()
        }

        return CorrectSelect(studentsList)
    }

    // migrated
    private suspend fun changeNews(news: NewsEntity): DatabaseResult {
        var result: DatabaseResult? = null
        referenceNews.child(news.id)
            .set(news) { error, _ ->
                if (error == null) result = Correct<Any>()
                else logRealtimeError(this, error)
            }

        return result ?: Error(R.string.error)
    }
}