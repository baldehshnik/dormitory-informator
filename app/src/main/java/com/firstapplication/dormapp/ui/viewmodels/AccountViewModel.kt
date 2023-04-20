package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.Correct
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.Error
import com.firstapplication.dormapp.sealed.Progress
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            setLiveValueWithMainContext(_userDataAccount, Progress)
            val result = repository.getVerifiedUser(studentVerifyModel.migrateToStudentModel())
            val data = when {
                result is Error || result == Progress -> result
                result is Correct<*> && result.value is StudentEntity -> Correct(result.value.migrateToStudentModel())
                else -> Error(R.string.error)
            }

            setLiveValueWithMainContext(_userDataAccount, data)
        }
    }

    fun addSavedNewsListener() = viewModelScope.launch(Dispatchers.IO) {
        val data = repository.readSavedNewsFromLocalDB().map { it.migrateToNewsModel() }
        setLiveValueWithMainContext(_savedNews, data)
    }
}