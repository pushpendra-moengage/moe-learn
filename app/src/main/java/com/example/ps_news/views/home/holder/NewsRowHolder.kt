package com.example.ps_news.views.home.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ps_news.R
import com.example.ps_news.utils.Helper
import com.example.ps_news.views.home.models.Article

class NewsRowHolder(var view: View, var callback: HolderCallback) : RecyclerView.ViewHolder(view) {

    private val ivPicture: ImageView = view.findViewById(R.id.iv_picture)
    private val tvTitle: TextView = view.findViewById(R.id.tv_title)
    private val tvDescription: TextView = view.findViewById(R.id.tv_description)
    private val tvAuthorName: TextView = view.findViewById(R.id.tv_author_name)
    private val tvSourceName: TextView = view.findViewById(R.id.tv_source_name)
    private val tvPublishedAt: TextView = view.findViewById(R.id.tv_published_at)

    fun setData(data: Article) {
        if(data.title != null)
            tvTitle.text = data.title
        view.setOnClickListener {
            callback.onNewsClick(data.url)
        }

        tvDescription.text = data.description
        if(!data.author.isNullOrEmpty())
            tvAuthorName.text = data.author
        if(!data.source?.name.isNullOrEmpty())
            tvSourceName.text = data.source?.name
        Helper.loadImage(ivPicture, data.urlToImage)
        if(!data.publishedAt.isNullOrEmpty())
            tvPublishedAt.text = Helper.getElapsedDateTime(data.publishedAt)
    }

    interface HolderCallback {
        fun onNewsClick(url: String?)
    }
}