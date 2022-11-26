package com.firstapplication.dormapp.ui.models

import com.firstapplication.dormapp.data.models.StudentVerifyEntity

data class StudentVerifyModel(
    val passNumber: Int,
    val roomNumber: Int,
    val password: String
) {

    fun migrateToStudentModel(): StudentVerifyEntity {
        return StudentVerifyEntity(passNumber, roomNumber, password)
    }

}