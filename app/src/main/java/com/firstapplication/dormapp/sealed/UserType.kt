package com.firstapplication.dormapp.sealed

sealed class UserType

object Student : UserType()

object Administrator : UserType()

object NoOne : UserType()