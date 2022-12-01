package com.firstapplication.dormapp.data.interfacies

import androidx.room.*
import com.firstapplication.dormapp.data.models.SavedNewsEntity
import com.firstapplication.dormapp.data.models.SavedNewsEntity.Companion.TABLE_NAME

@Dao
interface SavedNewsDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun readAllSavedNews(): List<SavedNewsEntity>

    @Insert
    suspend fun insertNewSavedNews(savedNewsEntity: SavedNewsEntity): Long

    @Delete
    suspend fun deleteSavedNews(savedNewsEntity: SavedNewsEntity)

    @Update
    suspend fun updateSavedNews(savedNewsEntity: SavedNewsEntity)

}