package com.firstapplication.dormapp.data.interfacies

import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.StudentEntity
import com.firstapplication.dormapp.sealed.DatabaseResult
import com.firstapplication.dormapp.sealed.SelectResult

interface AdminRepository : NewsAbilities {
    suspend fun readRespondedStudents(newsId: String): SelectResult
    suspend fun readNotRegisteredStudents(): SelectResult

    suspend fun confirmStudentRegistration(entity: StudentEntity): DatabaseResult
    suspend fun cancelStudentRegistration(pass: String): DatabaseResult

    suspend fun cancelRespondedStudent(newsId: String, pass: String): DatabaseResult
    suspend fun confirmRespondedStudent(newsId: String, studentEntity: StudentEntity): DatabaseResult
}

interface NewsAbilities {
    suspend fun readNewsFromDB(): SelectResult
    suspend fun addNews(news: NewsEntity): DatabaseResult
    suspend fun editNews(news: NewsEntity): DatabaseResult
    suspend fun deleteNews(id: String): DatabaseResult
}