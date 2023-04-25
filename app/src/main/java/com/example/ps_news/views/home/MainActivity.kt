package com.example.ps_news.views.home

import android.app.AlertDialog
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.media.AudioAttributes
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ps_news.App
import com.example.ps_news.R
import com.example.ps_news.utils.Helper
import com.example.ps_news.views.home.fragments.HomeFragment
import com.google.gson.Gson
import com.moengage.inapp.MoEInAppHelper
import com.moengage.inapp.model.SelfHandledCampaign
import com.moengage.inapp.model.SelfHandledCampaignData
import com.moengage.pushbase.MoEPushHelper

/**
 * Entry point for the app. the viewmodel is created for this activity and will be used in other related fragments
 */
class MainActivity : AppCompatActivity(), HomeFragment.FragmentCallback {

    private val TAG = "com.example.ps_news.views.home.MainActivity"
    lateinit var mainActivityViewModel: MainActivityViewModel
    lateinit var fragmentController : FragmentContainerView

    val receiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.let {
//                val data = p1.extras?.getString("data")
                val data = p1.getStringExtra("data")
                try {
                    val obj = Gson().fromJson(data, SelfHandledCampaignData::class.java)
                    showMyDialog(obj.campaign.payload.toString(), obj).show()
//                    MoEInAppHelper.getInstance().selfHandledShown(App.application!!, obj)
                } catch (e: java.lang.Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun showMyDialog(title: String?, data: SelfHandledCampaignData): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
//            MoEInAppHelper.getInstance()
//                .selfHandledClicked(App.application as Context, data)
        })
        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
//            MoEInAppHelper.getInstance().selfHandledDismissed(App.application as Context, data)
        })

        return builder
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(intent.extras != null){
            val extras = intent.extras
            if(extras!!.containsKey("screen")) { // <-- here screen is the key passed while creating the Push
                // Add your logic for getting screen name here and to open the required fragment
            }
        }

        savedInstanceState?.let {
            if(savedInstanceState.containsKey("screen")){
                val screenName = savedInstanceState.get("screen")
                val fullScreenName = "com.example.ps_news.views.home.fragments.$screenName"

                val FragmentClass = Class.forName(fullScreenName) as Fragment

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, FragmentClass)
                    .commit()
            }
        }

        checkForNotificationData()

        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        App.executors.networkIO().execute {
            mainActivityViewModel.fetchNewsData()
        }

        val filter = IntentFilter("SHOW_MY_DIALOG")

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)

//        createCustomNotificationChannel("SoundReal")
    }

    private fun createCustomNotificationChannel(channelName: String) {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val myChannel = manager.getNotificationChannel(channelName)
        if(myChannel == null) {


            val channel =
                NotificationChannel(channelName, channelName, NotificationManager.IMPORTANCE_HIGH)

            val soundUri =
                Uri.parse("android.resource://" + App.application?.packageName + "/" + R.raw.iphone_sound)

            soundUri?.let {
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                channel.setSound(soundUri, audioAttributes)
            }

            manager.createNotificationChannel(channel)
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

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
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