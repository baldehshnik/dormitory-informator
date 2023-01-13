package com.firstapplication.dormapp.ui.models

import android.os.Parcelable
import com.firstapplication.dormapp.data.models.NewsEntity
import com.firstapplication.dormapp.data.models.SavedNewsEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewsModel(
    val id: String,
    var imgSrc: String,
    var title: String,
    var timeType: String,
    var hours: Double,
    var description: String,
    var isActive: Boolean
) : Parcelable {

    fun migrateToNewsEntity() = NewsEntity(
        id = id,
        imgSrc = imgSrc,
        title = title,
        hours = hours,
        timeType = timeType,
        description = description,
        isActive = isActive
    )

    fun migrateToSavedNewsEntity() = SavedNewsEntity(
        id = id,
        imgSrc = imgSrc,
        title = title,
        hours = hours,
        timeType = timeType,
        description = description,
        isActive = isActive
    )

}