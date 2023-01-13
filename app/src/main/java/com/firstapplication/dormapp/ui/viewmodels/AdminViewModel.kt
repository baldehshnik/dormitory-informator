package com.firstapplication.dormapp.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.ChangeResult
import com.firstapplication.dormapp.sealed.ErrorResult
import com.firstapplication.dormapp.ui.models.NewsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias MutableLiveInsertResult = MutableLiveData<SingleEvent<ChangeResult>>
typealias LiveInsertResult = LiveData<SingleEvent<ChangeResult>>

@ActivityScope
class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _changedResult = MutableLiveInsertResult()
    val changedResult: LiveInsertResult = _changedResult

    fun createNews(
        id: String,
        title: String,
        img: Uri,
        time: String,
        timeType: String,
        description: String,
        edit: Boolean
    ) {
        val dTime: Double
        try {
            dTime = time.toDouble()
        } catch (e: Exception) {
            _changedResult.value = SingleEvent(ErrorResult(e.message ?: e.stackTraceToString()))
            return
        }

        val newsModel = NewsModel(
            id = id,
            imgSrc = img.toString(),
            title = title,
            hours = dTime,
            timeType = timeType,
            description = description,
            isActive = true
        )

        changedNewsResultListener()
        if (edit) {
            editNews(newsModel)
        } else {
            insertNews(newsModel)
        }
    }

    fun deleteNews(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            adminRepository.deleteNews(id)
        }
    }

    private fun editNews(newsModel: NewsModel) {
        viewModelScope.launch(Dispatchers.IO) {
            adminRepository.editNews(newsModel.migrateToNewsEntity())
        }
    }

    private fun insertNews(newsModel: NewsModel) {
        viewModelScope.launch(Dispatchers.IO) {
            adminRepository.addNews(newsModel.migrateToNewsEntity())
        }
    }

    private fun changedNewsResultListener() {
        viewModelScope.launch(Dispatchers.IO) {
            (adminRepository as AdminRepositoryImpl).changedNewsResult.collect {
                withContext(Dispatchers.Main) {
                    _changedResult.value = it
                }
            }
        }
    }
}