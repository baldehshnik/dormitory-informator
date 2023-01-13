package com.firstapplication.dormapp.ui.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.di.ActivityScope
import com.firstapplication.dormapp.ui.viewmodels.AdminViewModel
import com.firstapplication.dormapp.ui.viewmodels.ConfirmStudentsViewModel
import com.firstapplication.dormapp.ui.viewmodels.NewsListAdminViewModel
import javax.inject.Inject

@ActivityScope
class AdminVMFactory @Inject constructor(
    private val repository: AdminRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ConfirmStudentsViewModel::class.java) -> {
                ConfirmStudentsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NewsListAdminViewModel::class.java) -> {
                NewsListAdminViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                AdminViewModel(repository) as T
            }
            else -> {
                throw IllegalArgumentException("not found view model class")
            }
        }
    }

}