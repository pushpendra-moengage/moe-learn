package com.example.ps_news.views.home

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ps_news.App
import com.example.ps_news.R
import com.example.ps_news.utils.Helper
import com.example.ps_news.views.home.fragments.HomeFragment
import com.moengage.inapp.MoEInAppHelper
import com.moengage.pushbase.MoEPushHelper

/**
 * Entry point for the app. the viewmodel is created for this activity and will be used in other related fragments
 */
class MainActivity : AppCompatActivity(), HomeFragment.FragmentCallback {

    private val TAG = "com.example.ps_news.views.home.MainActivity"
    lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkForNotificationData()

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        App.executors.networkIO().execute {
            mainActivityViewModel.fetchNewsData()
        }
    }

    /**
     * This method will check the bundle when the app is opened and if the bundle contains the key "url"
     * that means the app is opened from a notification with url payload, and that payload url is opened
     * via the app
     */
    private fun checkForNotificationData() {
        val data = intent.extras

        if (data != null) {
            val urlFromPayload = data.getString("url")
            urlFromPayload?.let {
                Log.d("TOMATO", urlFromPayload)
                Helper.openURLInBrowser(this, urlFromPayload)
            }
        }
    }

    /**
     * OnClick handler for Fragment callback
     */
    override fun onNewsClick(url: String?) {
        Helper.openURLInBrowser(this, url)
    }

    override fun onStart() {
        super.onStart()
//        MoEInAppHelper.getInstance().showInApp(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
//        MoEInAppHelper.getInstance().onConfigurationChanged()
    }
}