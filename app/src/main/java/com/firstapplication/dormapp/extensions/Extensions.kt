package com.firstapplication.dormapp.extensions

import android.content.Context
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.di.AppComponent

val Context.appComponent: AppComponent
    get() = when(this) {
        is DormApp -> appComponent
        else -> this.applicationContext.appComponent
    }