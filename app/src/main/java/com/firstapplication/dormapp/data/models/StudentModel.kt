package com.firstapplication.dormapp.data.models

import android.os.Parcelable
import com.firstapplication.dormapp.ui.models.StudentVerifyModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentModel(
    val passNumber: Int = -1,
    val roomNumber: Int = -1,
    val password: String = ""
) : Parcelable {

    fun migrateToStudentVerifyModel(): StudentVerifyModel {
        return StudentVerifyModel(passNumber, roomNumber, password)
    }

}