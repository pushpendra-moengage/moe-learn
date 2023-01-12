package com.example.ps_news.utils

import android.app.*
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ps_news.App
import com.example.ps_news.R
import com.example.ps_news.views.home.Child_activity
import com.example.ps_news.views.home.MainActivity
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.internal.utils.getPendingIntentActivity
import com.moengage.pushbase.IS_DEFAULT_ACTION
import com.moengage.pushbase.MoEPushHelper
import com.moengage.pushbase.NAV_ACTION
import com.moengage.pushbase.model.NotificationPayload
import com.moengage.pushbase.model.action.NavigationAction
import com.moengage.pushbase.push.PushMessageListener

open class MyPushMessageListener(): PushMessageListener() {

        val CUSTOM_PAYLOAD_SHOW = "to_show"
        val SHOW_NOTIFICATION = "true"

    override fun onNotificationReceived(context: Context, payload: Bundle) {
//        Log.d("SIMILE_TRACK", "onNotificationReceivedonReceived")
        super.onNotificationReceived(context, payload)
//        Log.d("MOE_PUSH_LISTENER_RECEIVED", payload.toString())
//        MoEAnalyticsHelper.trackEvent(context, "MINE_NOTIFICATION_RECEIVED", Properties())
    }

    override fun isNotificationRequired(context: Context, payload: Bundle): Boolean {
//        Log.d("SIMILE_TRACK", "isNotificationRequired")
        val toShowNotification = super.isNotificationRequired(context, payload)
        if(toShowNotification && sharedPrefNotificationhandler(context)){
//            return true
            return payloadNotificationhandler(payload, context)
        } else {
            return false
        }
    }

    override fun getRedirectIntent(context: Context): Intent {
//        Log.d("SIMILE_TRACK", "getRedirectIntent")
        return super.getRedirectIntent(context)
    }

    override fun onCreateNotification(
        context: Context,
        notificationPayload: NotificationPayload
    ): NotificationCompat.Builder {
//        Log.d("SIMILE_TRACK", "onCreateNotification")
//        Log.d("MOE_CUSTOM_NOTIFICATION", notificationPayload.toString())

        if(notificationPayload.payload.containsKey("custom_notification") &&
            notificationPayload.payload.getString("custom_notification") == "true"){
            return MyNotificationManager(context, notificationPayload)
        } else {
            return super.onCreateNotification(context, notificationPayload)
        }

        return super.onCreateNotification(context, notificationPayload)
    }

    override fun customizeNotification(
        notification: Notification,
        context: Context,
        payload: Bundle
    ) {
//        Log.d("SIMILE_TRACK", "customizeNotification")
        super.customizeNotification(notification, context, payload)
    }

    override fun onPostNotificationReceived(context: Context, payload: Bundle) {
//        Log.d("SIMILE_TRACK", "onPostNotificationReceived")
        super.onPostNotificationReceived(context, payload)
    }

    override fun onNotificationClick(activity: Activity, payload: Bundle) {
//        super.onNotificationClick(activity, payload)
//        Log.d("SIMILE_TRACK", "onNotificationClick")
//        Log.d("MOE_NOTIFY_PAYLOAD", payload.toString())
//        MoEAnalyticsHelper.trackEvent(activity, "MINE_NOTIFICATION_CLICKED", Properties())
//        var redirectIntent: Intent? = null
        // -------------------------------------------------- //
//        if(payload.containsKey("open_activity") && payload.getString("open_activity") == "1"){
//            redirectIntent = Intent(activity, MainActivity::class.java)
//        } else {
//            redirectIntent = Intent(activity, Child_activity::class.java)
//        }

//        val redirectIntent = Intent(activity, MainActivity::class.java)
//        MoEPushHelper.getInstance().logNotificationClick(activity, payload)

//        activity.startActivity(redirectIntent)

        // -------------------------------------------------- //


        val isDefaultAction = payload.getBoolean(IS_DEFAULT_ACTION)
        var redirectIntent: Intent? = null
        if(isDefaultAction)
        {
            Log.d("MOE_NOTIFICATION_CLICK_DEFAULT", payload.toString())
            redirectIntent = Intent(activity, Child_activity::class.java)
        } else {
            Log.d("MOE_NOTIFICATION_CLICK_BUTTON", payload.toString())
            redirectIntent = Intent(activity, MainActivity::class.java)
            val action = payload.getParcelable(NAV_ACTION) as NavigationAction?
            Log.d("MOE_NOTIFICATION_CLICK_BUTTON_ACTION", action.toString())
        }

//        activity.startActivity(redirectIntent)
    }

    override fun onNotificationCleared(context: Context, payload: Bundle) {
//        Log.d("SIMILE_TRACK", "onNotificationCleared")
        super.onNotificationCleared(context, payload)
//        MoEAnalyticsHelper.trackEvent(context, "MINE_NOTIFICATION_CLEARED", Properties())
    }


    override fun getIntentFlags(payload: Bundle): Int {
//        Log.d("SIMILE_TRACK", "getIntentFlags")
        return super.getIntentFlags(payload)
    }

    override fun handleCustomAction(context: Context, payload: String) {
//        Log.d("MOE_SIMILE_TRACK", "handleCustomAction")
//        Log.d("MOE_SIMILE_TRACK", payload.toString())
//        super.handleCustomAction(context, payload)
        if(payload == "start_child_activity") {
            val intent = Intent(context, Child_activity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onNonMoEngageMessageReceived(context: Context, payload: Bundle) {
//        Log.d("SIMILE_TRACK", "onNonMoEngageMessageReceived")
        super.onNonMoEngageMessageReceived(context, payload)
    }

    override fun onNotificationNotRequired(context: Context, payload: Bundle) {
//        Log.d("SIMILE_TRACK", "onNotificationNotRequired")
        super.onNotificationNotRequired(context, payload)
    }

    //  ----------------------------------------------------------------------------- //

    private fun sharedPrefNotificationhandler(context: Context): Boolean{
        val userPref = context.getSharedPreferences("notification_toggle", MODE_PRIVATE)
        val userNotificationPref = userPref.getBoolean("show_notification", true)

        return userNotificationPref
    }

    private fun payloadNotificationhandler(payload: Bundle, context: Context): Boolean{
        if(payload.containsKey(CUSTOM_PAYLOAD_SHOW) && payload.getString(CUSTOM_PAYLOAD_SHOW) == SHOW_NOTIFICATION) {
//            Log.d("MOE_ONLINE_PAYLOAD", "true")
            return true
        }
        return false
    }

    private fun setRegularContent(builder: NotificationCompat.Builder, notificationPayload: NotificationPayload) {
        builder.setContentTitle("Hello " + notificationPayload.payload.getString("name") + " Ji")
        builder.setContentText("Kemcho??")
        builder.setSubText(notificationPayload.text.summary)

    }

    private fun setWelcomeContent(builder: NotificationCompat.Builder, notificationPayload: NotificationPayload) {
        builder.setContentTitle("Welcome " + notificationPayload.payload.getString("name") + " Ji")
        builder.setContentText("App mein appka swagat hai")
        builder.setSubText(notificationPayload.text.summary)
    }

    private fun MyNotificationManager(context: Context, notificationPayload: NotificationPayload): NotificationCompat.Builder {

//        Log.d("MOE_MY_NOTIFICATION", notificationPayload.toString())
        val channelName = notificationPayload.channelId
        val mChannel = NotificationChannel(channelName, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(mChannel)

        val builder = NotificationCompat.Builder(context, notificationPayload.channelId)
        if(notificationPayload.payload.containsKey("color")){
            val color = notificationPayload.payload.getString("color")
            if(color?.lowercase() == "red")
                builder.setColor(Color.RED)
            else if(color?.lowercase() == "green")
                builder.setColor(Color.GREEN)
            else
                builder.setColor(Color.GRAY)
        }

        if(notificationPayload.payload.containsKey("is_welcome_notification_type") && notificationPayload.payload.getString("is_welcome_notification_type") == "true"){
            setWelcomeContent(builder, notificationPayload)
        } else {
            setRegularContent(builder, notificationPayload)
        }

//        builder.setColor(Color.BLUE)
        builder.setSmallIcon(R.drawable.ic_app_icon_new)

//        val intent = Intent(context, Child_activity::class.java)
//        val pendingIntent = PendingIntent.getActivity(context, 12, intent, PendingIntent.FLAG_IMMUTABLE)

//        builder.setContentIntent(pendingIntent)

        return builder
    }
}