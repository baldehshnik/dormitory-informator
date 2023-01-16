package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.StudentVerifyEntity
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.LoginStudentResult
import com.firstapplication.dormapp.sealed.ProgressLoginResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class StudentLoginViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    private val _verifiedUser = MutableLiveData<LoginStudentResult>()
    val verifiedUser: LiveData<LoginStudentResult> get() = _verifiedUser

    fun checkUser(passNumber: Int, roomNumber: Int, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkStudentInDatabase(StudentVerifyEntity(
                passNumber = passNumber, roomNumber = roomNumber, password = password
            ))
            addVerifiedResult()
        }
    }

    private suspend fun addVerifiedResult() {
        (repository as StudentRepositoryImpl).verifiedUser.transformWhile { value ->
            emit(value)
            value == ProgressLoginResult
        }.collect {
            withContext(Dispatchers.Main) {
                _verifiedUser.value = it
            }
        }
    }
}