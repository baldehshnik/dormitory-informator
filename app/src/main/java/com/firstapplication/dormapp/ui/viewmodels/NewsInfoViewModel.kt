package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.ProgressResponse
import com.firstapplication.dormapp.sealed.ResponseResult
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class NewsInfoViewModel(
    private val adminRepository: AdminRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _responseResult = MutableLiveData<ResponseResult>()
    val responseResult: LiveData<ResponseResult> get() = _responseResult

    fun addWorker(id: String, userPass: Int) = viewModelScope.launch(Dispatchers.IO) {
        setLiveValueWithMainContext(_responseResult, ProgressResponse)
        val result = studentRepository.checkStudentAsWorkerOf(id, userPass)
        setLiveValueWithMainContext(_responseResult, result)
    }
}