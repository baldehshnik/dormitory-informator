package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.utils.Checker
import com.firstapplication.dormapp.utils.Checker.Companion.STUDENT_CHECK
import com.firstapplication.dormapp.ui.models.StudentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class RespondingStudentListViewModel(
    private val newsId: String,
    private val repository: AdminRepository
) : ViewModel() {

    private val _respondingStudentsResult = MutableLiveData<SelectResult>()
    val respondingStudentsResult: LiveData<SelectResult> get() = _respondingStudentsResult

    private val _confirmStudentResponse = MutableLiveData<DatabaseResult>()
    val confirmStudentResponse: LiveData<DatabaseResult> get() = _confirmStudentResponse

    fun confirmStudentResponse(newsId: String, student: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.confirmRespondedStudent(newsId, student.migrateToStudentEntity())
            addConfirmStudentResponseListener()
        }
    }

    fun cancelStudentResponse(newsId: String, passNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.cancelRespondedStudent(newsId, passNumber.toString())
            addConfirmStudentResponseListener()
        }
    }

    private fun addConfirmStudentResponseListener() = viewModelScope.launch {
        (repository as AdminRepositoryImpl).confirmStudentResponse.transformWhile { value ->
            emit(value)
            value == Progress
        }.collect { result ->
            withContext(Dispatchers.Main) {
                _confirmStudentResponse.value = result
            }
        }
    }

    private fun readRespondingStudents() = viewModelScope.launch(Dispatchers.IO) {
        repository.readRespondedStudents(newsId)
        addRespondingStudentsListener()
    }

    private fun addRespondingStudentsListener() = viewModelScope.launch {
        (repository as AdminRepositoryImpl).respondingStudent.transformWhile { value ->
            emit(value)
            value == ProgressSelect
        }.collect { result ->
            withContext(Dispatchers.Main) {
                val value = if (result is CorrectSelect<*>) {
                    Checker(result.value, STUDENT_CHECK).check()
                } else {
                    result
                }

                _respondingStudentsResult.value = value
            }
        }
    }

    init {
        readRespondingStudents()
    }
}