package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.ProgressResponse
import com.firstapplication.dormapp.sealed.ResponseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class NewsInfoViewModel(
    private val adminRepository: AdminRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _responseResult = MutableLiveData<ResponseResult>()
    val responseResult: LiveData<ResponseResult> get() = _responseResult

    fun addWorker(id: String, userPass: Int) = viewModelScope.launch(Dispatchers.IO) {
        studentRepository.checkStudentAsWorkerOf(id, userPass)
        registerResponseListener()
    }

    private suspend fun registerResponseListener() {
        (studentRepository as StudentRepositoryImpl).responseResult.transformWhile { value ->
            emit(value)
            value == ProgressResponse
        }.collect { value ->
            withContext(Dispatchers.Main) {
                _responseResult.value = value
            }
        }
    }
}