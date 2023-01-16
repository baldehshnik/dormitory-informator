package com.firstapplication.dormapp.sealed

sealed class LoginStudentResult

object ProgressLoginResult : LoginStudentResult()

object DeletedLoginResult : LoginStudentResult()

object DbErrorResult : LoginStudentResult()

object NotFoundResult : LoginStudentResult()

object NotVerifiedResult : LoginStudentResult()

object CorrectLoginResult : LoginStudentResult()