package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.SavedNewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.data.models.StudentVerifyEntity
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.LoginStudentResult
import com.firstapplication.dormapp.sealed.ResponseResult
import com.firstapplication.dormapp.sealed.SelectResult

interface StudentRepository : LocalStudentDatabaseRepository {
    suspend fun checkStudentInDatabase(studentVerifyEntity: StudentVerifyEntity): LoginStudentResult
    suspend fun getVerifiedUser(studentVerifyEntity: StudentVerifyEntity): DatabaseResult
    suspend fun getNews(): SelectResult
    suspend fun registerStudent(studentEntity: StudentEntity): DatabaseResult
    suspend fun checkStudentAsWorkerOf(id: String, userPass: Int): ResponseResult
}

interface LocalStudentDatabaseRepository {
    suspend fun readSavedNewsFromLocalDB(): List<SavedNewsEntity>
}