package com.firstapplication.dormapp.extensions

import android.content.Context
import androidx.lifecycle.ViewModel
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.di.AppComponent

val Context.appComponent: AppComponent
    get() = when(this) {
        is DormApp -> appComponent
        else -> this.applicationContext.appComponent
    }

fun checkListTypeIsStudentEntity(result: List<Any>): Boolean {
    result.forEach {
        if (it !is StudentEntity) return false
    }
    return true
}