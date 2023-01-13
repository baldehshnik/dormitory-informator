package com.firstapplication.dormapp.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.ui.viewmodels.RespondingStudentListViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException

class RespondingStudentsListVMFactory @AssistedInject constructor(
    @Assisted("newsId") private val newsId: String,
    private val repository: AdminRepository
) : ViewModelProvider.Factory {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("newsId") newsId: String): RespondingStudentsListVMFactory
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RespondingStudentListViewModel::class.java) -> createRespondingStudentViewModelAsT()
            else -> throw IllegalArgumentException("Unknown view model class")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : ViewModel> createRespondingStudentViewModelAsT(): T {
        return RespondingStudentListViewModel(repository = repository, newsId = newsId) as T
    }
}