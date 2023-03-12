package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.Correct
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.Error
import com.firstapplication.dormapp.sealed.Progress
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class AccountViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _userDataAccount = MutableLiveData<DatabaseResult>()
    val userDataAccount: LiveData<DatabaseResult> get() = _userDataAccount

    private val _savedNews = MutableLiveData<List<NewsModel>>()
    val savedNews: LiveData<List<NewsModel>> get() = _savedNews

    fun getVerifiedUser(studentVerifyModel: StudentVerifyModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getVerifiedUser(studentVerifyModel.migrateToStudentModel())
            userDataAccountListener()
        }
    }

    fun addSavedNewsListener() = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.readSavedNewsFromLocalDB().map { it.migrateToNewsModel() }
        withContext(Dispatchers.Main) {
            _savedNews.value = data
        }
    }

    private suspend fun userDataAccountListener() {
        (repository as StudentRepositoryImpl).userDataAccount.transformWhile { value ->
            emit(value)
            value == Progress
        }.collect {
            withContext(Dispatchers.Main) {
                _userDataAccount.value = when {
                    it is Error || it == Progress -> it
                    it is Correct<*> && it.value is StudentEntity -> Correct(it.value.migrateToStudentModel())
                    else -> Error(R.string.error)
                }
            }
        }
    }
}