package com.firstapplication.dormapp.ui.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.ui.viewmodels.StudentLoginViewModel
import com.firstapplication.dormapp.ui.viewmodels.StudentViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException


class StudentViewModelFactory @AssistedInject constructor(
    @Assisted("application") private val application: Application,
    private val repository: StudentRepositoryImpl
) : ViewModelProvider.AndroidViewModelFactory(application = application) {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("application") application: Application): StudentViewModelFactory
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StudentLoginViewModel::class.java) -> initStudentLoginViewModel()
            modelClass.isAssignableFrom(StudentViewModel::class.java) -> initStudentViewModel()
            else -> throw IllegalArgumentException("view model not found")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> initStudentLoginViewModel(): T {
        return StudentLoginViewModel(application = application, repository = repository) as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> initStudentViewModel(): T {
        return StudentViewModel(application = application, repository = repository) as T
    }
}