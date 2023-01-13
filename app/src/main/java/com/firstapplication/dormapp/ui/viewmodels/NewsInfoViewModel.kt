package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.ResponseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class NewsInfoViewModel(
    application: Application,
    private val adminRepository: AdminRepository,
    private val studentRepository: StudentRepository
) : AndroidViewModel(application) {

    private val _responseResult = MutableLiveData<ResponseResult>()
    val responseResult: LiveData<ResponseResult> get() = _responseResult

    fun addWorker(id: String, userPass: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            registerResponseListener()
            studentRepository.checkStudentAsWorkerOf(id, userPass)
        }
    }

    private suspend fun registerResponseListener() {
        viewModelScope.launch {
            (studentRepository as StudentRepositoryImpl).responseResult.collect { value ->
                val response = value.getValue()!!
                _responseResult.value = response
            }
        }
    }

}