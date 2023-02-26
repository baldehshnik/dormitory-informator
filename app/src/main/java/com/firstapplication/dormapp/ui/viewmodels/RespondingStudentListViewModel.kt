package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.extensions.checkListTypeIsStudentEntity
import com.firstapplication.dormapp.sealed.*
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

    private val _confirmStudentResponse = MutableLiveData<ChangeResult>()
    val confirmStudentResponse: LiveData<ChangeResult> get() = _confirmStudentResponse

    fun confirmStudentResponse(newsId: String, student: StudentModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.confirmStudentResponse(newsId, student.migrateToStudentEntity())
            addConfirmStudentResponseListener()
        }
    }

    fun cancelStudentResponse(newsId: String, passNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.cancelStudentResponse(newsId, passNumber.toString())
            addConfirmStudentResponseListener()
        }
    }

    private fun addConfirmStudentResponseListener() = viewModelScope.launch {
        (repository as AdminRepositoryImpl).confirmStudentResponse.transformWhile { value ->
            emit(value)
            value == ProgressResult
        }.collect { result ->
            _confirmStudentResponse.value = result
        }
    }

    private fun readRespondingStudents() = viewModelScope.launch(Dispatchers.IO) {
        repository.readRespondingStudents(newsId)
        addRespondingStudentsListener()
    }

    private fun addRespondingStudentsListener() = viewModelScope.launch {
        (repository as AdminRepositoryImpl).respondingStudent.transformWhile { value ->
            emit(value)
            value == ProgressSelect
        }.collect { result ->
            if (result is CorrectSelect<*>) {
                isStudentsList(result)
            } else {
                _respondingStudentsResult.value = result
            }
        }
    }

    private suspend fun migrateStudentsEntities(entities: List<StudentEntity>) = withContext(Dispatchers.Default) {
        val models = entities.map {
            it.migrateToStudentModel()
        }
        withContext(Dispatchers.Main) {
            _respondingStudentsResult.value = CorrectSelect(models)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun isStudentsList(result: CorrectSelect<*>) {
        val res = result.value
        val isStudentEntitiesList = checkListTypeIsStudentEntity(res)
        when {
            res.isEmpty() -> _respondingStudentsResult.value = Empty
            isStudentEntitiesList -> migrateStudentsEntities(res as List<StudentEntity>)
            else -> _respondingStudentsResult.value = ErrorSelect
        }
    }

    init {
        readRespondingStudents()
    }
}