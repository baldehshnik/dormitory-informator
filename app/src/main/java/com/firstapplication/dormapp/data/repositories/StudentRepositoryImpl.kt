package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.*
import com.firstapplication.dormapp.data.remote.*
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

    override fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity) {
        val rootReference = database.reference
        val userReference =
            rootReference.child(PACKAGE_USERS).child(studentVerifyEntity.passNumber.toString())

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val passNumber = dataSnapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
                    val roomNumber = dataSnapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
                    val password =
                        dataSnapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""

                    val user = StudentVerifyEntity(passNumber, roomNumber, password)
                    if (user != studentVerifyEntity) _verifiedUser.value = SingleEvent(value = -1)
                    else _verifiedUser.value = SingleEvent(value = 1)
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

    override fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity) {
        val rootReference = database.reference
        val userReference =
            rootReference.child(PACKAGE_USERS).child(studentVerifyEntity.passNumber.toString())

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val passNumber = dataSnapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
                    val roomNumber = dataSnapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
                    val password =
                        dataSnapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""

                    val user = StudentVerifyEntity(passNumber, roomNumber, password)
                    if (user != studentVerifyEntity) {
                        _userDataAccount.value = SingleEvent(StudentEntity(passNumber = 0))
                    } else {
                        val studentEntity = StudentEntity(
                            passNumber = user.passNumber,
                            roomNumber = user.roomNumber,
                            password = password
                        )

                        val fullName =
                            dataSnapshot.child(FULL_NAME_KEY).getValue(String::class.java)
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
}