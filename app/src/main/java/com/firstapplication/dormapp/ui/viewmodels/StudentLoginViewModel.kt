package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.StudentVerifyEntity
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.LoginStudentResult
import com.firstapplication.dormapp.sealed.ProgressLoginResult
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class StudentLoginViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _verifiedUser = MutableLiveData<LoginStudentResult>()
    val verifiedUser: LiveData<LoginStudentResult> get() = _verifiedUser

    fun checkUser(passNumber: Int, roomNumber: Int, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            setLiveValueWithMainContext(_verifiedUser, ProgressLoginResult)
            val result = repository.checkStudentInDatabase(StudentVerifyEntity(passNumber, roomNumber, password))
            setLiveValueWithMainContext(_verifiedUser, result)
        }
    }
}