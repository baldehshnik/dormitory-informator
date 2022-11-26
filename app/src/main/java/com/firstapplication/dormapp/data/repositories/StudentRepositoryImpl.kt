package com.firstapplication.dormapp.data.repositories

import android.util.Log
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.models.StudentVerifyEntity
import com.firstapplication.dormapp.ui.models.StudentModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class StudentRepositoryImpl @Inject constructor() : StudentRepository {

    private val _verifiedUser = MutableStateFlow(SingleEvent(value = 0))
    val verifiedUser: StateFlow<SingleEvent<Int>> get() = _verifiedUser.asStateFlow()

    private val _userDataAccount = MutableStateFlow(SingleEvent(StudentEntity()))
    val userDataAccount: StateFlow<SingleEvent<StudentEntity>> get() = _userDataAccount.asStateFlow()

    override fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity) {
        val rootReference = Firebase.database.reference
        val userReference = rootReference.child(PACKAGE_USERS).child(studentVerifyEntity.passNumber.toString())

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val passNumber = dataSnapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
                    val roomNumber = dataSnapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
                    val password = dataSnapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""

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
        val rootReference = Firebase.database.reference
        val userReference = rootReference.child(PACKAGE_USERS).child(studentVerifyEntity.passNumber.toString())

        userReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val passNumber = dataSnapshot.child(PASS_KEY).getValue(Int::class.java) ?: 0
                    val roomNumber = dataSnapshot.child(ROOM_KEY).getValue(Int::class.java) ?: 0
                    val password = dataSnapshot.child(PASSWORD_KEY).getValue(String::class.java) ?: ""

                    val user = StudentVerifyEntity(passNumber, roomNumber, password)
                    if (user != studentVerifyEntity) {
                        _userDataAccount.value = SingleEvent(StudentEntity(passNumber = 0))
                    }
                    else {
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

    companion object {
        private const val PACKAGE_USERS = "students"

        private const val PASS_KEY = "passNumber"
        private const val ROOM_KEY = "roomNumber"
        private const val PASSWORD_KEY = "password"
        private const val FULL_NAME_KEY = "fullName"
        private const val HOURS_KEY = "hours"
    }
}