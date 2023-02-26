package com.firstapplication.dormapp.ui.models

import android.os.Parcelable
import com.firstapplication.dormapp.data.models.StudentEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentModel(
    val fullName: String,
    var imgSrc: String = "android.resource//drawable/ic_baseline_no_image",
    val passNumber: Int,
    val roomNumber: Int,
    val hours: Double,
    val password: String
) : Parcelable {

    fun migrateToStudentEntity(): StudentEntity {
        return StudentEntity(fullName, imgSrc, passNumber, roomNumber, hours, password)
    }

    companion object {
        const val NAME_DELIMITER = "||DEL|M|TER||"
    }
}