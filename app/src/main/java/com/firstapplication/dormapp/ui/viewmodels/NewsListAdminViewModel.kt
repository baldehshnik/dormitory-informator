package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.models.NewsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@ActivityScope
class NewsListAdminViewModel(
    application: Application,
    private val repository: AdminRepository
) : AndroidViewModel(application) {

    private val _news = MutableLiveData<List<NewsModel>>()
    val news: LiveData<List<NewsModel>> get() = _news

    private fun readNewsFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readNewsFromDB()
        }
    }

    private fun addNewsListener() {
        viewModelScope.launch(Dispatchers.IO) {
            (repository as AdminRepositoryImpl).newsData.collect { newsEntities ->
                val data = newsEntities.map { it.migrateToNewsModel() }.toMutableList()

                if (data.size == 1 && data[0].id.isBlank()) {
                    return@collect
                }

                withContext(Dispatchers.Main) {
                    _news.value = data
                }
            }
        }
    }

    init {
        addNewsListener()
        readNewsFromDB()
    }
}