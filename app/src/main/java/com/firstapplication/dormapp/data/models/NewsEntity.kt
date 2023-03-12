package com.firstapplication.dormapp.data.models

import com.firstapplication.dormapp.ui.models.NewsModel

data class NewsEntity(
    var id: String = "",
    var imgSrc: String = "",
    var title: String = "",
    var hours: Double = 0.0,
    val timeType: String = "",
    var description: String = "",
    var isActive: Boolean = false
) {

    fun migrateToNewsModel() : NewsModel {
        return NewsModel(id, imgSrc, title, timeType, hours, description, isActive)
    }

}