package com.firstapplication.dormapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.NewsItemBinding
import com.firstapplication.dormapp.ui.models.NewsModel

class NewsAdapter : ListAdapter<NewsModel, NewsAdapter.NewsViewHolder>(NewsDiffUtil()) {

    class NewsViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(newsModel: NewsModel) = with(binding) {
            twNewsTitle.text = newsModel.title
            twNewsHours.text = newsModel.hours.toString()
            imgNewsIcon.setImageResource(R.drawable.ic_baseline_newspaper)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NewsViewHolder(NewsItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}