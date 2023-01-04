package com.example.ps_news

import android.app.Application
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoEngage
import com.moengage.core.config.LogConfig

class ApplicationClass : Application() {

    val APP_ID = ""

    override fun onCreate() {
        super.onCreate()
        App.init(this)

        val moEngage = MoEngage.Builder(this, APP_ID)
            .configureLogs(LogConfig(LogLevel.VERBOSE, true))
//            .enableEncryption()
            .setDataCenter(DataCenter.DATA_CENTER_1)
            .build()

        MoEngage.initialiseDefaultInstance(moEngage)
    }
}