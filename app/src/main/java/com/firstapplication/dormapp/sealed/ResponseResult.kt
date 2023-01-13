package com.firstapplication.dormapp.sealed

sealed class ResponseResult

object ErrorResponse: ResponseResult()

object ProgressResponse : ResponseResult()

object CorrectResponse : ResponseResult()

object AlreadyRegisteredResponse: ResponseResult()