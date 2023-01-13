package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.NewsEntity

interface AdminRepository {
    suspend fun readNewsFromDB()
    suspend fun addNews(news: NewsEntity)
    suspend fun editNews(news: NewsEntity)
    suspend fun deleteNews(id: String)
    suspend fun readRespondingStudents(newsId: String)
    suspend fun readNotRegisteredStudents()
    suspend fun confirmStudent(pass: String)
    suspend fun cancelStudent(pass: String)
}