package com.firstapplication.dormapp.data.models

import com.firstapplication.dormapp.ui.models.StudentModel

data class StudentEntity (
    var fullName: String = "None None None",
    val imgSrc: String = "android.resource//drawable/ic_baseline_no_image",
    val passNumber: Int = -1,
    val roomNumber: Int = -1,
    var hours: Double = 0.0,
    val password: String = ""
) {

    fun migrateToStudentModel(): StudentModel {
        return StudentModel(fullName, imgSrc, passNumber, roomNumber, hours, password)
    }
}