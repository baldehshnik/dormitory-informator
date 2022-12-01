package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.models.NewsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class NewsListViewModel (
    application: Application,
    private val repository: StudentRepository
) : AndroidViewModel(application) {

    private val _news = MutableLiveData<List<NewsModel>>()
    val news: LiveData<List<NewsModel>> get() = _news

    fun readNewsFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getNews()
        }
    }

    private fun setNewsListener() {
        viewModelScope.launch(Dispatchers.IO) {
            (repository as StudentRepositoryImpl).newsData.collect {
                val value = it.map { e -> e.migrateToNewsModel() }
                withContext(Dispatchers.Main) {
                    _news.value = value
                }
            }
        }
    }

    init {
        setNewsListener()
    }
}