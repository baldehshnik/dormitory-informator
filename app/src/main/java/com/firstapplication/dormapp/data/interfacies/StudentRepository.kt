package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.SavedNewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.models.StudentVerifyEntity

interface StudentRepository {
    fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity)
    fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity)
    fun getNews()
    suspend fun readSavedNewsFromLocalDB(): List<SavedNewsEntity>
    suspend fun registerStudent(studentEntity: StudentEntity)
    suspend fun checkStudentAsWorkerOf(id: String, userPass: Int)
}