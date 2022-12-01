package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.models.StudentModel
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class AccountViewModel(
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    private val _userDataAccount = MutableStateFlow(StudentEntity().migrateToStudentModel())
    val userDataAccount: StateFlow<StudentModel> get() = _userDataAccount.asStateFlow()

    private val _savedNews = MutableLiveData<List<NewsModel>>()
    val savedNews: LiveData<List<NewsModel>> get() = _savedNews

    fun getVerifiedUser(studentVerifyModel: StudentVerifyModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getVerifiedUser(studentVerifyEntity = studentVerifyModel.migrateToStudentModel())
        }
    }

    fun addSavedNewsListener() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.readSavedNewsFromLocalDB().map { it.migrateToNewsModel() }
            withContext(Dispatchers.Main) {
                _savedNews.value = data
            }
        }
    }

    private fun userDataAccountListener() {
        viewModelScope.launch(Dispatchers.IO) {
            (repository as StudentRepositoryImpl).userDataAccount.collect {
                val value = it.getValue()?.migrateToStudentModel() ?: return@collect
                _userDataAccount.value = value
            }
        }
    }


    init {
        userDataAccountListener()
    }

}