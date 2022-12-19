package com.firstapplication.dormapp.extensions

import android.content.Context
import android.content.SharedPreferences
import com.firstapplication.dormapp.DormApp
import com.firstapplication.dormapp.di.AppComponent
import com.firstapplication.dormapp.ui.activity.MainActivity.Companion.LOGIN_KEY
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.PASSWORD_KEY
import com.firstapplication.dormapp.ui.fragments.login.StudentLoginFragment.Companion.ROOM_KEY

val Context.appComponent: AppComponent
    get() = when (this) {
        is DormApp -> appComponent
        else -> this.applicationContext.appComponent
    }

fun SharedPreferences.saveStudent(
    passStr: String,
    roomStr: String,
    password: String
) = this.edit()
    .putString(LOGIN_KEY, passStr)
    .putInt(ROOM_KEY, roomStr.toInt())
    .putString(PASSWORD_KEY, password)
    .apply()
