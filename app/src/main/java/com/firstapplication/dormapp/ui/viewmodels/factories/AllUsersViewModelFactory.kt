package com.firstapplication.dormapp.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.ui.viewmodels.NewsInfoViewModel
import javax.inject.Inject

class AllUsersViewModelFactory @Inject constructor(
    private val adminRepository: AdminRepository,
    private val studentRepository: StudentRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsInfoViewModel::class.java) -> createNewsInfoViewModelAsT()
            else -> throw IllegalArgumentException("Unknown view model class")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewModel> createNewsInfoViewModelAsT(): T {
        return NewsInfoViewModel(adminRepository, studentRepository) as T
    }

}