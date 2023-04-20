package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.CorrectSelect
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.ProgressSelect
import com.firstapplication.dormapp.sealed.SelectResult
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.utils.Checker
import com.firstapplication.dormapp.utils.Checker.Companion.STUDENT_CHECK
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class RespondingStudentListViewModel(
    private val newsId: String,
    private val repository: AdminRepository
) : ViewModel() {

    private val _respondingStudentsResult = MutableLiveData<SelectResult>()
    val respondingStudentsResult: LiveData<SelectResult> get() = _respondingStudentsResult

    private val _confirmStudentResponse = MutableLiveData<DatabaseResult>()
    val confirmStudentResponse: LiveData<DatabaseResult> get() = _confirmStudentResponse

    // need update
    fun confirmStudentResponse(newsId: String, student: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.confirmRespondedStudent(newsId, student.migrateToStudentEntity())
            setLiveValueWithMainContext(_confirmStudentResponse, result)
        }
    }

    fun cancelStudentResponse(newsId: String, passNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.cancelRespondedStudent(newsId, passNumber.toString())
            setLiveValueWithMainContext(_confirmStudentResponse, result)
        }
    }

    private fun readRespondingStudents() = viewModelScope.launch(Dispatchers.IO) {
        setLiveValueWithMainContext(_respondingStudentsResult, ProgressSelect)
        val data = when(val result = repository.readRespondedStudents(newsId)) {
            is CorrectSelect<*> -> Checker(result.value, STUDENT_CHECK).check()
            else -> result
        }

        setLiveValueWithMainContext(_respondingStudentsResult, data)
    }

    init {
        readRespondingStudents()
    }
}