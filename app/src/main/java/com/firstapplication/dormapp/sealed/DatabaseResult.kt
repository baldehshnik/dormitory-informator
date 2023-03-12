package com.firstapplication.dormapp.sealed

import androidx.annotation.StringRes

sealed class DatabaseResult

class Error(@StringRes val message: Int) : DatabaseResult()

object Progress : DatabaseResult()

class Correct<T>(val value: T? = null) : DatabaseResult()