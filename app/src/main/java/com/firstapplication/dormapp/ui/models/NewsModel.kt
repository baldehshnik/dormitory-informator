package com.firstapplication.dormapp.ui.models

import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.SavedNewsEntity

data class NewsModel(
    val id: String,
    var imgSrc: String,
    var title: String,
    var hours: Double,
    var description: String,
    var isActive: Boolean
) {

    fun migrateToNewsEntity() = NewsEntity(
        id = id,
        imgSrc = imgSrc,
        title = title,
        hours = hours,
        description = description,
        isActive = isActive
    )

    fun migrateToSavedNewsEntity() = SavedNewsEntity(
        id = id,
        imgSrc = imgSrc,
        title = title,
        hours = hours,
        description = description,
        isActive = isActive
    )

}