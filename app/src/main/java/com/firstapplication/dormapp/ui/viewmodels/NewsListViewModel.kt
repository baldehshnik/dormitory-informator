package com.firstapplication.dormapp.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.CorrectSelect
import com.firstapplication.dormapp.sealed.ProgressSelect
import com.firstapplication.dormapp.sealed.SelectResult
import com.firstapplication.dormapp.utils.Checker
import com.firstapplication.dormapp.utils.Checker.Companion.NEWS_CHECK
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class NewsListViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _news = MutableLiveData<SelectResult>()
    val news: LiveData<SelectResult> get() = _news

    fun readNewsFromDB() = viewModelScope.launch(Dispatchers.IO) {
        setLiveValueWithMainContext(_news, ProgressSelect)
        val data = when(val result = repository.getNews()) {
            is CorrectSelect<*> -> Checker(result.value, NEWS_CHECK).check()
            else -> result
        }

        setLiveValueWithMainContext(_news, data)
    }
}