package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity

interface AdminRepository {
    suspend fun readNewsFromDB()

    suspend fun addNews(news: NewsEntity)
    suspend fun editNews(news: NewsEntity)
    suspend fun deleteNews(id: String)

    suspend fun readRespondedStudents(newsId: String)

    suspend fun readNotRegisteredStudents()

    suspend fun confirmStudentRegistration(entity: StudentEntity)
    suspend fun cancelStudentRegistration(pass: String)

    suspend fun cancelRespondedStudent(newsId: String, pass: String)
    suspend fun confirmRespondedStudent(newsId: String, studentEntity: StudentEntity)
}