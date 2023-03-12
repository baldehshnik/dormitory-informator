package com.firstapplication.dormapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentVerifyEntity(
    val passNumber: Int = -1,
    val roomNumber: Int = -1,
    val password: String = ""
) : Parcelable