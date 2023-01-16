package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity

interface AdminRepository {
    suspend fun readNewsFromDB()
    suspend fun addNews(news: NewsEntity)
    suspend fun editNews(news: NewsEntity)
    suspend fun deleteNews(id: String)
    suspend fun readRespondingStudents(newsId: String)
    suspend fun readNotRegisteredStudents()
    suspend fun confirmStudent(entity: StudentEntity)
    suspend fun cancelStudent(pass: String)
}