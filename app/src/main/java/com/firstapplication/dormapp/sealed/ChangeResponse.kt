package com.firstapplication.dormapp.sealed

import androidx.annotation.StringRes

sealed class ChangeResponse

class Error(@StringRes val message: Int): ChangeResponse()

object Progress : ChangeResponse()

object Correct : ChangeResponse()