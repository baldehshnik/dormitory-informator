package com.firstapplication.dormapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.firstapplication.dormapp.data.models.SavedNewsEntity.Companion.TABLE_NAME
import com.firstapplication.dormapp.ui.models.NewsModel

@Entity(tableName = TABLE_NAME)
data class SavedNewsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = "",

    @ColumnInfo(name = "image_source")
    var imgSrc: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "hours")
    var hours: Double = 0.0,

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "is_active")
    var isActive: Boolean = false
) {

    fun migrateToNewsModel() = NewsModel(
        id = id,
        imgSrc = imgSrc,
        title = title,
        hours = hours,
        description = description,
        isActive = isActive
    )

    companion object {
        const val TABLE_NAME = "news"
    }

}