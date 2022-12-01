package com.firstapplication.dormapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.models.SavedNewsEntity

@Database(entities = [SavedNewsEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract val newsDao: SavedNewsDao

    companion object {
        const val DATABASE_NAME = "news_database"
    }
}