package com.example.ps_news

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.moengage.core.DataCenter
import com.moengage.core.LogLevel
import com.moengage.core.MoECoreHelper
import com.moengage.core.MoEngage
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.config.LogConfig
import com.moengage.core.listeners.AppBackgroundListener
import com.moengage.core.model.AppBackgroundData
import com.moengage.core.model.AppStatus

class ApplicationClass : Application() {

    val APP_ID = ""
    val NOT_INSTALLED = -1
    val CURRENT_VERSION = 1

    override fun onCreate() {
        super.onCreate()
        App.init(this)

        val moEngage = MoEngage.Builder(this, APP_ID)
            .configureLogs(LogConfig(LogLevel.VERBOSE, true))
//            .enableEncryption()
            .setDataCenter(DataCenter.DATA_CENTER_1)
            .build()

        MoEngage.initialiseDefaultInstance(moEngage)

        val pref = getSharedPreferences("APP_INFO", MODE_PRIVATE)
        val versionNo = pref.getInt("CURRENT_APP_VERSION", -1);

        if(versionNo == NOT_INSTALLED){
            // Fresh install
            val edit = pref.edit()
            edit.putInt("CURRENT_APP_VERSION", 1).apply();
            MoEAnalyticsHelper.setAppStatus(this, AppStatus.INSTALL)
        } else if(BuildConfig.VERSION_CODE == CURRENT_VERSION){
            // Normal login
        } else {
            // Update
            MoEAnalyticsHelper.setAppStatus(this, AppStatus.UPDATE)
        }

        MoECoreHelper.addAppBackgroundListener(object : AppBackgroundListener {
            override fun onAppBackground(context: Context, data: AppBackgroundData) {
                Log.d("MOE_TIMBER", "Going in background")
            }

        })
    }
}