package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.StudentVerifyEntity

interface StudentRepository {
    fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity)
    fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity)
}