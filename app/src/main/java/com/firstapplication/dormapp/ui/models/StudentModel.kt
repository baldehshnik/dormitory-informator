package com.firstapplication.dormapp.ui.models

import android.os.Parcelable
import com.firstapplication.dormapp.data.models.StudentEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentModel(
    val fullName: String,
    val passNumber: Int,
    val roomNumber: Int,
    val hours: Double,
    val password: String
) : Parcelable {

    fun migrateToStudentEntity(): StudentEntity {
        return StudentEntity(fullName, passNumber, roomNumber, hours, password)
    }

}