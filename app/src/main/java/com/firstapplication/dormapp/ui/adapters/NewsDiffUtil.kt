package com.firstapplication.dormapp.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.firstapplication.dormapp.ui.models.NewsModel

class NewsDiffUtil : DiffUtil.ItemCallback<NewsModel>() {
    override fun areItemsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: NewsModel, newItem: NewsModel): Boolean {
        return oldItem.id == newItem.id
    }
}