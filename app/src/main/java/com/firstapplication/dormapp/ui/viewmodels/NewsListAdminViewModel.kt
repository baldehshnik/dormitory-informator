package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.CorrectSelect
import com.firstapplication.dormapp.sealed.ProgressSelect
import com.firstapplication.dormapp.sealed.SelectResult
import com.firstapplication.dormapp.utils.Checker
import com.firstapplication.dormapp.utils.Checker.Companion.NEWS_CHECK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class NewsListAdminViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _news = MutableLiveData<SelectResult>()
    val news: LiveData<SelectResult> get() = _news

    private fun readNewsFromDB() = viewModelScope.launch(Dispatchers.IO) {
        repository.readNewsFromDB()
        addNewsListener()
    }

    private suspend fun addNewsListener() {
        (repository as AdminRepositoryImpl).newsData.transformWhile { value ->
            emit(value)
            value == ProgressSelect
        }.collect { event ->
            withContext(Dispatchers.Main) {
                val value = if (event is CorrectSelect<*>) {
                    Checker(event.value, NEWS_CHECK).check()
                } else {
                    event
                }

                _news.value = value
            }
        }
    }

    init {
        readNewsFromDB()
    }
}