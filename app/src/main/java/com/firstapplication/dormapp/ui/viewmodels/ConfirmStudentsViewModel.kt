package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.extensions.checkListTypeIsStudentEntity
import com.firstapplication.dormapp.sealed.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmStudentsViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _notSavedStudentsResult = MutableLiveData<SelectResult>()
    val notSavedStudentsResult: LiveData<SelectResult> get() = _notSavedStudentsResult

    private val _confirmResult = MutableLiveData<ChangeResult>()
    val confirmResult: LiveData<ChangeResult> get() = _confirmResult

    fun readNotConfirmedStudents() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readNotRegisteredStudents()
        }
    }

    fun confirmStudent(pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.confirmStudent(pass)
            setConfirmResultListener()
        }
    }

    fun cancelStudent(pass: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.cancelStudent(pass)
            setConfirmResultListener()
        }
    }

    private fun addNotConfirmedStudentListener() {
        viewModelScope.launch {
            (repository as AdminRepositoryImpl).notRegisteredStudentsResult.collect {
                if (it is Correct<*>) {
                    isStudentsList(it)
                } else {
                    _notSavedStudentsResult.value = it
                }
            }
        }
    }

    private suspend fun setConfirmResultListener() {
        (repository as AdminRepositoryImpl).confirmResult.transformWhile { value ->
            emit(value)
            value !is CorrectResult || value !is ErrorResult
        }.collect {
            withContext(Dispatchers.Main) {
                _confirmResult.value = it
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun isStudentsList(result: Correct<*>) {
        val res = result.value
        val isStudentEntitiesList = checkListTypeIsStudentEntity(res)
        _notSavedStudentsResult.value = when {
            res.isEmpty() -> Empty
            isStudentEntitiesList -> Correct((res as List<StudentEntity>).map {
                it.migrateToStudentModel()
            })
            else -> Error
        }
    }

    init {
        addNotConfirmedStudentListener()
    }
}