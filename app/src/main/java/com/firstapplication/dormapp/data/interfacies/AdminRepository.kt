package com.firstapplication.dormapp.data.interfacies

interface AdminRepository {
    suspend fun readNewsFromDB()
}