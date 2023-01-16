package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.models.StudentEntity
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
class AdminRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : AdminRepository {

    private var changedValueId = ""

    private val _newsData = MutableStateFlow(listOf(NewsEntity()))
    val newsData: StateFlow<List<NewsEntity>> get() = _newsData.asStateFlow()

    private val _changedNewsResult =
        MutableStateFlow<SingleEvent<ChangeResult>>(SingleEvent(ProgressResult))
    val changedNewsResult: StateFlow<SingleEvent<ChangeResult>> get() = _changedNewsResult.asStateFlow()

    private val _respondingStudents = MutableStateFlow<SelectResult>(ProgressSelect)
    val respondingStudent: StateFlow<SelectResult> get() = _respondingStudents.asStateFlow()

    private val _notRegisteredStudentsResult = MutableStateFlow<SelectResult>(ProgressSelect)
    val notRegisteredStudentsResult: StateFlow<SelectResult> get() = _notRegisteredStudentsResult.asStateFlow()

    private val _confirmResult = MutableStateFlow<ChangeResult>(ProgressResult)
    val confirmResult: StateFlow<ChangeResult> get() = _confirmResult.asStateFlow()

    private val changedNewsListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                _changedNewsResult.value = SingleEvent(CorrectResult)
                database.reference
                    .child(PACKAGE_NEWS)
                    .child(changedValueId)
                    .removeEventListener(this)

                changedValueId = ""
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.i(this@AdminRepositoryImpl::class.java.simpleName, "$changedValueId not exists")
        }
    }

    override suspend fun readNewsFromDB() {
        val newsReference = database.reference.child(PACKAGE_NEWS)

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

    override suspend fun addNews(news: NewsEntity) {
        val reference = database.reference.child(PACKAGE_NEWS)
        changedValueId = reference.push().key.toString()
        news.id = changedValueId
        changeNews(news)
    }

    override suspend fun editNews(news: NewsEntity) {
        changedValueId = news.id
        changeNews(news)
    }

    override suspend fun deleteNews(id: String) {
        val reference = database.reference.child(PACKAGE_NEWS).child(id)
        reference.removeValue()
    }

    override suspend fun readRespondingStudents(newsId: String) {
        val reference = database.reference.child(PACKAGE_NEWS).child(newsId).child(PACKAGE_RESPONSE)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                val respondingStudentsIdList = mutableListOf<Int>()
                for (snapshot in snapshots.children) {
                    val entity = snapshot.getValue(Int::class.java)
                    if (entity != null) respondingStudentsIdList.add(entity)
                }
                readRespondingStudentsByIds(respondingStudentsIdList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(this@AdminRepositoryImpl::class.java.simpleName, error.message)
                _respondingStudents.value = ErrorSelect
            }
        })
    }

    override suspend fun readNotRegisteredStudents() {
        val reference = database.reference.child(PACKAGE_REGISTER)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val students = mutableListOf<StudentEntity>()
                for (s in snapshot.children) {
                    val value = s.getValue<StudentEntity>()
                    val confirm = s.child(CONFIRM).getValue<Int>()
                    if (value != null && confirm == ConfirmedRegistration.EMPTY.value) {
                        students.add(value)
                    }
                }
                _notRegisteredStudentsResult.value = CorrectSelect(students)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(this@AdminRepositoryImpl::class.java.simpleName, error.message)
                _notRegisteredStudentsResult.value = ErrorSelect
            }
        })
    }

    override suspend fun confirmStudent(entity: StudentEntity) {
        val pass = entity.passNumber.toString()
        database.reference.child(PACKAGE_USERS).child(pass).setValue(entity)
        _confirmResult.value = CorrectResult
        database.reference.child(PACKAGE_REGISTER).child(pass).removeValue()
    }

    override suspend fun cancelStudent(pass: String) {
        database.reference.child(PACKAGE_REGISTER)
            .child(pass)
            .child(CONFIRM)
            .setValue(ConfirmedRegistration.NOT_CONFIRMED.value)

        _confirmResult.value = CorrectResult
    }

    fun clearRespondingStudentsResult() {
        _respondingStudents.value = ProgressSelect
    }

    private fun readRespondingStudentsByIds(list: List<Int>) {
        val studentsList = mutableListOf<StudentEntity>()
        if (list.isEmpty()) {
            _respondingStudents.value = CorrectSelect(studentsList)
            return
        }

        var n = 0
        var isError = false
        list.forEach {
            if (isError) return

            val reference = database.reference.child(PACKAGE_USERS).child(it.toString())
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(StudentEntity::class.java)
                    if (value != null) studentsList.add(value)

                    n++
                    if (n == list.size) {
                        _respondingStudents.value = CorrectSelect(studentsList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(this@AdminRepositoryImpl::class.java.simpleName, error.message)
                    isError = true
                    _respondingStudents.value = ErrorSelect
                }
            })
        }
    }

    private fun changeNews(news: NewsEntity) {
        val reference = database.reference.child(PACKAGE_NEWS).child(changedValueId)
        reference.apply {
            setValue(news)
            addValueEventListener(changedNewsListener)
        }
    }
}