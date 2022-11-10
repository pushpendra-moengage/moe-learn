package com.example.ps_news.views.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ps_news.R
import com.example.ps_news.views.home.holder.NewsRowHolder
import com.example.ps_news.views.home.models.Article

class NewsFeedAdapter(var callback: AdapterCallback) : RecyclerView.Adapter<NewsRowHolder>(),
    NewsRowHolder.HolderCallback {

    var articleList: List<Article?> = ArrayList<Article>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsRowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vh_news_row, parent, false)
        return NewsRowHolder(view, this)
    }

    override fun onBindViewHolder(holder: NewsRowHolder, position: Int) {
        articleList?.get(position)?.let { holder.setData(it) }
    }

    override fun getItemCount(): Int {
//        Log.d("TAMATAR", articleList.size.toString())
        return articleList.size
    }

    override fun onNewsClick(url: String?) {
        callback.onNewsClick(url)
    }

    interface AdapterCallback {
        fun onNewsClick(url: String?)
    }
}