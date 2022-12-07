package com.firstapplication.dormapp.sealed

sealed class ChangeResult

class ErrorResult(val message: String) : ChangeResult()

object ProgressResult : ChangeResult()

object CorrectResult : ChangeResult()