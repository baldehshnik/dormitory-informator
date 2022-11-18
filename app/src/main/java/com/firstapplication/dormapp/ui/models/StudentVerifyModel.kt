package com.firstapplication.dormapp.ui.models

import com.firstapplication.dormapp.data.models.StudentModel

data class StudentVerifyModel(
    val passNumber: Int,
    val roomNumber: Int,
    val password: String
) {

    fun migrateToStudentModel(): StudentModel {
        return StudentModel(passNumber, roomNumber, password)
    }

}