package com.firstapplication.dormapp

import android.app.Application
import com.firstapplication.dormapp.di.AppComponent
import com.firstapplication.dormapp.di.DaggerAppComponent

const val ADMIN_KEY = "1111"

class DormApp : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .context(applicationContext)
            .build()
    }
}