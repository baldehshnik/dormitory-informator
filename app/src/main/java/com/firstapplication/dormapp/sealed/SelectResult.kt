package com.firstapplication.dormapp.sealed

sealed class SelectResult

object Error : SelectResult()

object Progress : SelectResult()

object Empty : SelectResult()

class Correct<T: List<Any>>(val value: T) : SelectResult()