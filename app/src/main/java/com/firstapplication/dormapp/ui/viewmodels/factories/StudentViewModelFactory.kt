package com.firstapplication.dormapp.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.repositories.StudentRepositoryImpl
import com.firstapplication.dormapp.ui.viewmodels.AccountViewModel
import com.firstapplication.dormapp.ui.viewmodels.NewsListViewModel
import com.firstapplication.dormapp.ui.viewmodels.StudentLoginViewModel
import com.firstapplication.dormapp.ui.viewmodels.StudentRegisterViewModel
import javax.inject.Inject

class StudentViewModelFactory @Inject constructor(
    private val repository: StudentRepositoryImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(StudentLoginViewModel::class.java) -> initStudentLoginViewModel()
            modelClass.isAssignableFrom(AccountViewModel::class.java) -> initStudentViewModel()
            modelClass.isAssignableFrom(NewsListViewModel::class.java) -> initNewsListViewModel()
            modelClass.isAssignableFrom(StudentRegisterViewModel::class.java) -> initStudentRegisterViewModelAsT()
            else -> throw IllegalArgumentException("view model not found")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> initStudentRegisterViewModelAsT(): T {
        return StudentRegisterViewModel(repository = repository) as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> initNewsListViewModel(): T {
        return NewsListViewModel(repository = repository) as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> initStudentLoginViewModel(): T {
        return StudentLoginViewModel(repository = repository) as T
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: ViewModel> initStudentViewModel(): T {
        return AccountViewModel(repository = repository) as T
    }
}