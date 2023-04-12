package com.firstapplication.dormapp.ui.interfacies

import com.firstapplication.dormapp.ui.models.NewsModel

interface OnAdminNewsItemClickListener {
    fun onFullItemClick(news: NewsModel)
    fun onEditClick(news: NewsModel)
    fun onLongClick(selectedItemPosition: Int): Boolean
}