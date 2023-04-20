package com.firstapplication.dormapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
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

class ConfirmStudentsViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _notSavedStudentsResult = MutableLiveData<SelectResult>()
    val notSavedStudentsResult: LiveData<SelectResult> get() = _notSavedStudentsResult

    private val _confirmResult = MutableLiveData<DatabaseResult>()
    val confirmResult: LiveData<DatabaseResult> get() = _confirmResult

    fun readNotConfirmedStudents() = viewModelScope.launch(Dispatchers.IO) {
        setLiveValueWithMainContext(_notSavedStudentsResult, ProgressSelect)
        val data = when (val result = repository.readNotRegisteredStudents()) {
            is CorrectSelect<*> -> Checker(result.value, STUDENT_CHECK).check()
            else -> result
        }

        setLiveValueWithMainContext(_notSavedStudentsResult, data)
    }

    fun confirmStudent(model: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.confirmStudentRegistration(model.migrateToStudentEntity())
            Log.i("ANTHOTER", "result took")
            setLiveValueWithMainContext(_confirmResult, result)
        }
    }

    fun cancelStudent(pass: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = repository.cancelStudentRegistration(pass)
        setLiveValueWithMainContext(_confirmResult, result)
    }
}