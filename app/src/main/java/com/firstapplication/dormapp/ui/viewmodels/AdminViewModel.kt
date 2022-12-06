package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.models.SingleEvent
import com.firstapplication.dormapp.data.repositories.AdminRepositoryImpl
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.ErrorResult
import com.firstapplication.dormapp.sealed.InsertResult
import com.firstapplication.dormapp.ui.models.NewsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias MutableLiveInsertResult = MutableLiveData<SingleEvent<InsertResult>>
typealias LiveInsertResult = LiveData<SingleEvent<InsertResult>>

@ActivityScope
class AdminViewModel(
    application: Application,
    private val adminRepository: AdminRepository
) : AndroidViewModel(application) {

    private val _createResult = MutableLiveInsertResult()
    val createResult: LiveInsertResult = _createResult

    fun createNews(title: String, img: Uri, time: String, timeType: String, description: String) {
        val dTime: Double
        try {
            dTime = time.toDouble()
        } catch (e: Exception) {
            _createResult.value = SingleEvent(ErrorResult(e.message ?: e.stackTraceToString()))
            return
        }

        val newsModel = NewsModel(
            id = "",
            imgSrc = img.toString(),
            title = title,
            hours = dTime,
            timeType = timeType,
            description = description,
            isActive = true
        )

        insertNewsResultListener()
        insertNews(newsModel)
    }

    private fun insertNews(newsModel: NewsModel) {
        viewModelScope.launch(Dispatchers.IO) {
            adminRepository.addNews(newsModel.migrateToNewsEntity())
        }
    }

    private fun insertNewsResultListener() {
        viewModelScope.launch(Dispatchers.IO) {
            (adminRepository as AdminRepositoryImpl).insertNewsResult.collect {
                withContext(Dispatchers.Main) {
                    _createResult.value = it
                }
            }
        }
    }
}