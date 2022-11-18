package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.StudentModel

interface StudentRepository {
    fun checkStudentInDatabase(studentModel: StudentModel)
    fun getVerifiedUser(studentModel: StudentModel)
}