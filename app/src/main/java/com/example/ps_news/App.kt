package com.example.ps_news

import android.app.Application
import android.content.Context
import com.example.ps_news.utils.AppExecutors
import com.google.gson.Gson
import com.moengage.core.MoEngage

object App {
    var executors = AppExecutors()
    var gson: Gson = Gson()
    var application: Context? = null
    lateinit var moEngage: MoEngage


    fun init(context: Context) {
        application = context
    }
}