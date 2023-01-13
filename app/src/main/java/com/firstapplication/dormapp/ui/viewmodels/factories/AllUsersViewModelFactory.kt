package com.firstapplication.dormapp.ui.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.data.interfacies.StudentRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.viewmodels.NewsInfoViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AllUsersViewModelFactory @AssistedInject constructor(
    @Assisted("application") private val application: Application,
    private val adminRepository: AdminRepository,
    private val studentRepository: StudentRepository
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @AssistedFactory
    @ActivityScope
    interface Factory {
        fun create(@Assisted("application") application: Application): AllUsersViewModelFactory
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewsInfoViewModel::class.java) -> createNewsInfoViewModelAsT()
            else -> throw IllegalArgumentException("Unknown view model class")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewModel> createNewsInfoViewModelAsT(): T {
        return NewsInfoViewModel(application, adminRepository, studentRepository) as T
    }

}