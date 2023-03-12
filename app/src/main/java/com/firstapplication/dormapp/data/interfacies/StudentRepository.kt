package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.SavedNewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.models.StudentVerifyEntity

interface StudentRepository : LocalStudentDatabaseRepository {
    suspend fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity)
    suspend fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity)
    suspend fun getNews()
    suspend fun registerStudent(studentEntity: StudentEntity)
    suspend fun checkStudentAsWorkerOf(id: String, userPass: Int)
}

interface LocalStudentDatabaseRepository {
    suspend fun readSavedNewsFromLocalDB(): List<SavedNewsEntity>
}