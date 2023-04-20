package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.CorrectSelect
import com.firstapplication.dormapp.sealed.SelectResult
import com.firstapplication.dormapp.utils.Checker
import com.firstapplication.dormapp.utils.Checker.Companion.NEWS_CHECK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class NewsListAdminViewModel(
    private val repository: AdminRepository
) : ViewModel() {

    private val _news = MutableLiveData<SelectResult>()
    val news: LiveData<SelectResult> get() = _news

    private fun readNewsFromDB() = viewModelScope.launch(Dispatchers.IO) {
        val data = when(val result = repository.readNewsFromDB()) {
            is CorrectSelect<*> -> Checker(result.value, NEWS_CHECK).check()
            else -> result
        }

        withContext(Dispatchers.Main) {
            _news.value = data
        }
    }

    init {
        readNewsFromDB()
    }
}