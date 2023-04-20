package com.firstapplication.dormapp.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.Error
import com.firstapplication.dormapp.sealed.Progress
import com.firstapplication.dormapp.ui.models.NewsModel
import com.firstapplication.dormapp.utils.setLiveValueWithMainContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ActivityScope
class AdminViewModel(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _changedResult = MutableLiveData<DatabaseResult>()
    val changedResult: LiveData<DatabaseResult> = _changedResult

    fun createNews(
        id: String, title: String, imgUri: Uri,
        time: String, timeType: String,
        description: String, edit: Boolean
    ) {
        val dTime: Double
        try {
            dTime = time.toDouble()
        } catch (e: Exception) {
            _changedResult.value = Error(R.string.error)
            return
        }

        val newsModel = NewsModel(
            id = id,
            imgSrc = imgUri.toString(),
            title = title,
            hours = dTime,
            timeType = timeType,
            description = description,
            isActive = true
        )

        if (edit) editNews(newsModel)
        else insertNews(newsModel)

//        changedNewsResultListener()
    }

    fun deleteNews(id: String) = viewModelScope.launch(Dispatchers.IO) {
        val result = adminRepository.deleteNews(id)
    }

    private fun editNews(newsModel: NewsModel) = viewModelScope.launch(Dispatchers.IO) {
        setLiveValueWithMainContext(_changedResult, Progress)
        val result = adminRepository.editNews(newsModel.migrateToNewsEntity())
        setLiveValueWithMainContext(_changedResult, result)
    }

    private fun insertNews(newsModel: NewsModel) = viewModelScope.launch(Dispatchers.IO) {
        setLiveValueWithMainContext(_changedResult, Progress)
        val result = adminRepository.addNews(newsModel.migrateToNewsEntity())
        setLiveValueWithMainContext(_changedResult, result)
    }

//    private fun changedNewsResultListener() = viewModelScope.launch(Dispatchers.IO) {
//        (adminRepository as AdminRepositoryImpl).changedNewsResult.transformWhile { value ->
//            emit(value)
//            value == Progress
//        }.collect {
//            withContext(Dispatchers.Main) {
//                _changedResult.value = it
//            }
//        }
//    }
}