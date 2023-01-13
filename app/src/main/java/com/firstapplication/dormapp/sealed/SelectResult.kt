package com.firstapplication.dormapp.sealed

sealed class SelectResult

object ErrorSelect : SelectResult()

object ProgressSelect : SelectResult()

object Empty : SelectResult()

class CorrectSelect<T: List<Any>>(val value: T) : SelectResult()