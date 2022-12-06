package com.firstapplication.dormapp.data.models

import com.firstapplication.dormapp.ui.models.NewsModel

data class NewsEntity(
    val id: String = "",
    var imgSrc: String = "",
    var title: String = "",
    var hours: Double = 0.0,
    val timeType: String = "",
    var description: String = "",
    var isActive: Boolean = false
) {

    fun migrateToNewsModel() = NewsModel(
        id = id,
        imgSrc = imgSrc,
        title = title,
        hours = hours,
        timeType = timeType,
        description = description,
        isActive = isActive
    )

}