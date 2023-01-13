package com.firstapplication.dormapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.NewsAdminItemBinding
import com.firstapplication.dormapp.ui.interfacies.OnAdminNewsItemClickListener
import com.firstapplication.dormapp.ui.models.NewsModel

class NewsAdminAdapter(
    private val listener: OnAdminNewsItemClickListener
) : ListAdapter<NewsModel, NewsAdminAdapter.NewsAdminViewHolder>(NewsDiffUtil()) {

    class NewsAdminViewHolder(private val binding: NewsAdminItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(newsModel: NewsModel) = with(binding) {
            twNewsHours.text = "${newsModel.hours} ${newsModel.timeType}"
            twNewsTitle.text = newsModel.title

            Glide.with(binding.imgNewsIcon)
                .load(newsModel.imgSrc.toUri())
                .placeholder(R.drawable.ic_baseline_broken_image)
                .into(binding.imgNewsIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NewsAdminViewHolder(NewsAdminItemBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NewsAdminViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.onFullItemClick(item)
        }

        holder.itemView.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
            listener.onEditClick(item)
        }
    }
}