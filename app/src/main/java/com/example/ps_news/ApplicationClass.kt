package com.example.ps_news

import android.app.Application

class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()
        App.init(this)
    }
}