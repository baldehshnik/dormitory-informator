package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.sealed.*
import com.firstapplication.dormapp.utils.Checker
import com.firstapplication.dormapp.utils.Checker.Companion.STUDENT_CHECK
import com.firstapplication.dormapp.ui.models.StudentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmStudentsViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _notSavedStudentsResult = MutableLiveData<SelectResult>()
    val notSavedStudentsResult: LiveData<SelectResult> get() = _notSavedStudentsResult

    private val _confirmResult = MutableLiveData<DatabaseResult>()
    val confirmResult: LiveData<DatabaseResult> get() = _confirmResult

    fun readNotConfirmedStudents() = viewModelScope.launch(Dispatchers.IO) {
        repository.readNotRegisteredStudents()
        addNotConfirmedStudentListener()
    }

    fun confirmStudent(model: StudentModel) = viewModelScope.launch(Dispatchers.IO) {
        repository.confirmStudentRegistration(model.migrateToStudentEntity())
        setConfirmResultListener()
    }

    fun cancelStudent(pass: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.cancelStudentRegistration(pass)
        setConfirmResultListener()
    }

    private suspend fun addNotConfirmedStudentListener() {
        (repository as AdminRepositoryImpl).notRegisteredStudentsResult.transformWhile { value ->
            emit(value)
            value == ProgressSelect
        }.collect { result ->
            withContext(Dispatchers.Main) {
                val value = if (result is CorrectSelect<*>) {
                    Checker(result.value, STUDENT_CHECK).check()
                } else {
                    result
                }

                _notSavedStudentsResult.value = value
            }
        }
    }

    private suspend fun setConfirmResultListener() {
        (repository as AdminRepositoryImpl).confirmResult.transformWhile { value ->
            emit(value)
            value == Progress
        }.collect {
            withContext(Dispatchers.Main) {
                _confirmResult.value = it
            }
        }
    }
}