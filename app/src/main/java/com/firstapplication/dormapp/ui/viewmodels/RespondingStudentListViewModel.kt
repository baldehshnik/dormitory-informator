package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.Correct
import com.firstapplication.dormapp.sealed.Empty
import com.firstapplication.dormapp.sealed.Error
import com.firstapplication.dormapp.sealed.SelectResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class RespondingStudentListViewModel(
    private val newsId: String,
    private val repository: AdminRepository
) : ViewModel() {

    private val _respondingStudentsResult = MutableLiveData<SelectResult>()
    val respondingStudentsResult: LiveData<SelectResult> get() = _respondingStudentsResult

    private fun readRespondingStudents() {
        viewModelScope.launch(Dispatchers.IO) {
            repository
            repository.readRespondingStudents(newsId)
            addRespondingStudentsListener()
        }
    }

    private fun addRespondingStudentsListener() {
        viewModelScope.launch {
            (repository as AdminRepositoryImpl).respondingStudent.collect { result ->
                if (result is Correct<*>) {
                    isStudentsList(result)
                } else {
                    _respondingStudentsResult.value = result
                }
            }
        }
    }

    private fun migrateStudentsEntities(entities: List<StudentEntity>) {
        val models = entities.map {
            it.migrateToStudentModel()
        }
        _respondingStudentsResult.value = Correct(models)
    }

    @Suppress("UNCHECKED_CAST")
    private fun isStudentsList(result: Correct<*>) {
        val res = result.value
        val isStudentEntitiesList = checkResultTypeIsStudentEntity(res)
        when {
            res.isEmpty() -> _respondingStudentsResult.value = Empty
            isStudentEntitiesList -> migrateStudentsEntities(res as List<StudentEntity>)
            else -> _respondingStudentsResult.value = Error
        }
    }

    private fun checkResultTypeIsStudentEntity(result: List<Any>): Boolean {
        result.forEach {
            if (it !is StudentEntity) return false
        }
        return true
    }

    override fun onCleared() {
        (repository as AdminRepositoryImpl).clearRespondingStudentsResult()
        super.onCleared()
    }

    init {
        readRespondingStudents()
    }
}