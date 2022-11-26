package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.models.StudentVerifyEntity
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ActivityScope
class StudentLoginViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    val verifiedUser: StateFlow<SingleEvent<Int>> get() = (repository as StudentRepositoryImpl).verifiedUser

    fun checkUser(passNumber: Int, roomNumber: Int, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkStudentInDatabase(StudentVerifyEntity(
                passNumber = passNumber, roomNumber = roomNumber, password = password
            ))
        }
    }

}