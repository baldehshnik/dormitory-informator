package com.firstapplication.dormapp.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.firstapplication.dormapp.data.interfacies.AdminRepository
import com.firstapplication.dormapp.di.ActivityScope

@ActivityScope
class AdminViewModel(
    application: Application,
    private val adminRepository: AdminRepository
) : AndroidViewModel(application) {

}