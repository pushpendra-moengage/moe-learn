package com.example.ps_news

import android.content.Context
import com.example.ps_news.utils.AppExecutors
import com.google.gson.Gson

object App {
    var executors = AppExecutors()
    var gson: Gson = Gson()
    var application: Context? = null

    fun init(context: Context) {
        application = context
    }
}