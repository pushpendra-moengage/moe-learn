package com.example.ps_news.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ps_news.App
import com.example.ps_news.R
import com.example.ps_news.views.home.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.pushbase.MoEPushHelper

/**
 *  This class will be handling the notification coming from Firebase
 *  onMessageReceived handles message whenever the notification is sent from the FCM Console
 *
 *  Implementation
 *  As payload two things are passed
 *      -   a key "url" having link to some url
 *      -   an image for showing in notification
 *
 *      If the payload contains the key url, sendNotification() will create an intent out
 *      and pass the "url" in bundle for the intent.
 *
 *      When MainActivity is opened from notification and the key "url" is present in the bundle,
 *      then app will open that url in browser(if it can be handled by)
 *
 * **/
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * This method is called whenever a notification is recieved by the system for the app
     **/
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        Log.d("MOE_ON_MESSAGE", message.data.toString())

        if(MoEPushHelper.getInstance().isFromMoEngagePlatform(message.data)){
            if(MoEPushHelper.getInstance().isSilentPush(message.data)){
//                Log.d("MOE_PUSH_SILENT_DATA", message.data.toString())
                MoEAnalyticsHelper.trackEvent(App.application!!, "SILENT_PUSH", Properties())
            }
//            MoEPushHelper.getInstance().logNotificationReceived(App.application!!, message.data)
//            MoEPushHelper.getInstance().isSilentPush(message.data)
            MoEFireBaseHelper.getInstance().passPushPayload(App.application!!, message.data)
        }

        return

        Log.d("TAMATAR", message.notification.toString())

        if (message.data.isNotEmpty()) {
            Log.d("TAMATAR", "Mesage payload ${message.data}")
        }

        message?.notification?.let {
            Log.d("TAMATAR", "Message notification ${it.body}")
        }

//        sendNotification(message)

    }

    /**
     * For creating intent
     */
    private fun sendNotification(message: RemoteMessage) {
        val notificationIntent = Intent(this, MainActivity::class.java)

        message.data?.let {
            notificationIntent.putExtra("url", it.get("url"))
        }

        val pendingIntent =
            PendingIntent.getActivity(this, 123, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        val channelName = "ps_news_channel"

        val notification = NotificationCompat.Builder(this, channelName)
            .setSmallIcon(R.drawable.ic_app_icon_new)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelName,
                "This is test channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(123, notification.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MOE_TAMATAR TOKEN", token)

        /*
         On recieving a new token, save it in sharedpref so that it can be used in
         Application class everytime the app opens
         */
        val token_pref_edit = getSharedPreferences("token_pref", MODE_PRIVATE).edit()
        token_pref_edit.putString("token", token).apply()

        MoEFireBaseHelper.getInstance().passPushToken(App.application!!, token)

    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()

    }

}