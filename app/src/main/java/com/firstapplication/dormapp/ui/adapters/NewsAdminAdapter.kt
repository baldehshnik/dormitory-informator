package com.firstapplication.dormapp.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firstapplication.dormapp.R
import com.firstapplication.dormapp.databinding.NewsAdminItemBinding
import com.firstapplication.dormapp.ui.interfacies.OnAdminNewsItemClickListener
import com.firstapplication.dormapp.ui.models.NewsModel

class NewsAdminAdapter(
    private val entities: MutableList<NewsModel>,
    private val listener: OnAdminNewsItemClickListener
) : ListAdapter<NewsModel, NewsAdminAdapter.NewsAdminViewHolder>(NewsDiffUtil()) {

    class NewsAdminViewHolder(
        private val binding: NewsAdminItemBinding,
        private val listener: OnAdminNewsItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(newsModel: NewsModel) = with(binding) {
            twNewsHours.text = "${newsModel.hours} ${newsModel.timeType}"
            twNewsTitle.text = newsModel.title

            Glide.with(binding.imgNewsIcon)
                .load(newsModel.imgSrc.toUri())
                .placeholder(R.drawable.ic_baseline_broken_image)
                .into(binding.imgNewsIcon)

            root.setOnClickListener { listener.onFullItemClick(newsModel) }
            root.setOnLongClickListener { listener.onLongClick(adapterPosition) }
            btnEdit.setOnClickListener { listener.onEditClick(newsModel) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdminViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NewsAdminViewHolder(NewsAdminItemBinding.inflate(inflater, parent, false), listener)
    }

    override fun onBindViewHolder(holder: NewsAdminViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount(): Int {
        return entities.size
    }

    fun removeSelectedItem(selectedItem: Int) {
        if (selectedItem >= 0 && selectedItem < entities.size) {
            entities.removeAt(selectedItem)
            notifyItemRemoved(selectedItem)
        }
    }
}