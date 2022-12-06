package com.firstapplication.dormapp.sealed

sealed class InsertResult

class ErrorResult(val message: String) : InsertResult()

object ProgressResult : InsertResult()

object CorrectResult : InsertResult()