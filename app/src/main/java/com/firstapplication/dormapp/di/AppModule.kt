package com.firstapplication.dormapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.firstapplication.dormapp.data.interfacies.SavedNewsDao
import com.firstapplication.dormapp.data.local.NewsDatabase
import com.firstapplication.dormapp.data.local.NewsDatabase.Companion.DATABASE_NAME
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    fun provideRealtimeDatabase(): FirebaseDatabase {
        return Firebase.database
    }

    @Singleton
    @Provides
    fun provideNewsDatabase(context: Context): NewsDatabase {
        return Room.databaseBuilder(
            context,
            NewsDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideSavedNewsDao(newsDatabase: NewsDatabase): SavedNewsDao {
        return newsDatabase.newsDao
    }

}