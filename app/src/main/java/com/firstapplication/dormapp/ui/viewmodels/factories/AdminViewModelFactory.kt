package com.firstapplication.dormapp.ui.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment
import com.firstapplication.dormapp.ui.viewmodels.AdminViewModel
import com.firstapplication.dormapp.ui.viewmodels.StudentLoginViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.lang.IllegalArgumentException

class AdminViewModelFactory @AssistedInject constructor(
    @Assisted("application") private val application: Application,
    private val adminRepository: AdminRepository
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @AssistedFactory
    @ActivityScope
    interface Factory {
        fun create(@Assisted("application") application: Application): AdminViewModelFactory
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java))
            return createAdminViewModelAsT()
        throw IllegalArgumentException("Unknown view model class")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewModel> createAdminViewModelAsT(): T {
        return AdminViewModel(application, adminRepository) as T
    }

}