package com.firstapplication.dormapp.data.models

import com.firstapplication.dormapp.ui.models.StudentModel

data class StudentEntity (
    var fullName: String = "None None None",
    val passNumber: Int = -1,
    val roomNumber: Int = -1,
    var hours: Double = 0.0,
    val password: String = ""
) {

    fun migrateToStudentModel(): StudentModel {
        return StudentModel(fullName, passNumber, roomNumber, hours, password)
    }

}