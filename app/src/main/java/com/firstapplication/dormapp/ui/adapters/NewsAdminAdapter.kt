package com.firstapplication.dormapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapplication.dormapp.databinding.NewsAdminItemBinding
import com.firstapplication.dormapp.ui.models.NewsModel

class NewsAdminAdapter : ListAdapter<NewsModel, NewsAdminAdapter.NewsAdminViewHolder>(NewsDiffUtil()) {

    class NewsAdminViewHolder(private val binding: NewsAdminItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsModel: NewsModel) = with(binding) {
            twNewsHours.text = newsModel.hours.toString()
            twNewsTitle.text = newsModel.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NewsAdminViewHolder(NewsAdminItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NewsAdminViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}