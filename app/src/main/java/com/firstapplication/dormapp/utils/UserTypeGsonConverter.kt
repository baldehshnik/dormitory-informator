package com.firstapplication.dormapp.utils

import com.firstapplication.dormapp.sealed.Administrator
import com.firstapplication.dormapp.sealed.NoOne
import com.firstapplication.dormapp.sealed.Student
import com.firstapplication.dormapp.sealed.UserType
import com.google.gson.Gson

class UserTypeGsonConverter {

    fun toGson(value: UserType): String {
        val convertedValue = when (value) {
            Student -> "Student"
            Administrator -> "Administrator"
            NoOne -> "NoOne"
        }

        return Gson().toJson(convertedValue)
    }

    fun fromGson(value: String): UserType {
        val convertedValue = when (Gson().fromJson(value, String::class.java)) {
            "Administrator" -> Administrator
            "Student" -> Student
            else -> NoOne
        }

        return convertedValue
    }
}