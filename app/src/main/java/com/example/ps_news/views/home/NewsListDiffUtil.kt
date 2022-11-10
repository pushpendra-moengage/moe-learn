package com.example.ps_news.views.home

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.example.ps_news.App
import com.example.ps_news.views.home.models.Article

class NewsListDiffUtil(var mOldList: List<Article?>, var mNewList: List<Article?>): DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return mOldList.size
    }

    override fun getNewListSize(): Int {
        return mNewList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        if(mOldList[oldItemPosition]?.title != mNewList[newItemPosition]?.title)
            return false

        if(mOldList[oldItemPosition]?.url != mNewList[newItemPosition]?.url)
            return false

        if(mOldList[oldItemPosition]?.description != mNewList[newItemPosition]?.description)
            return false

        if(mOldList[oldItemPosition]?.author != mNewList[newItemPosition]?.author)
            return false

        if(mOldList[oldItemPosition]?.urlToImage != mNewList[newItemPosition]?.urlToImage)
            return false

        if(mOldList[oldItemPosition]?.publishedAt != mNewList[newItemPosition]?.publishedAt)
            return false

        if(mOldList[oldItemPosition]?.source?.name != mNewList[newItemPosition]?.source?.name)
            return false

        return true
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val isEqual = TextUtils.equals(App.gson.toJson(mOldList[oldItemPosition]), App.gson.toJson(mNewList[newItemPosition]))
        return isEqual
    }
}