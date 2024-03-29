package com.example.ps_news

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ps_news.utils.MyPushMessageListener
import com.example.ps_news.views.home.fragments.HomeFragment
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.moengage.core.*
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.config.FcmConfig
import com.moengage.core.config.InAppConfig
import com.moengage.core.config.LogConfig
import com.moengage.core.config.NotificationConfig
import com.moengage.core.listeners.AppBackgroundListener
import com.moengage.core.model.AppBackgroundData
import com.moengage.core.model.AppStatus
import com.moengage.core.model.IntegrationPartner
import com.moengage.firebase.MoEFireBaseHelper
//import com.moengage.geofence.MoEGeofenceHelper
import com.moengage.inapp.MoEInAppHelper
import com.moengage.inapp.listeners.SelfHandledAvailableListener
import com.moengage.inapp.model.SelfHandledCampaignData
import com.moengage.push.amp.plus.MiPushHelper
import com.moengage.pushbase.MoEPushHelper
import com.moengage.pushbase.listener.TokenAvailableListener
import com.moengage.pushbase.model.Token
import com.xiaomi.channel.commonutils.android.Region

//import com.segment.analytics.kotlin.destinations.moengage.MoEngageDestination
//import com.segment.analytics.kotlin.android.Analytics
//import com.segment.analytics.kotlin.core.Analytics
//import com.segment.analytics.kotlin.core.Traits
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.put

class ApplicationClass : Application(), LifecycleEventObserver {

    val APP_ID = "8SIW681S80Z08KSHQFSTIZ8T"
    val NOT_INSTALLED = -1
    val appKey = "5432117747547"
    val appId = "2882303761521177547"

    override fun onCreate() {
        super.onCreate()
        App.init(this)

//        val analytics = Analytics("1fApOtyI8HNfZX5WzjpL2q9fHqT6gaxn", this) {
//            trackApplicationLifecycleEvents = true
//            flushAt = 5
//        }
//        analytics.add(MoEngageDestination(this))

        val moEngage = MoEngage.Builder(this, APP_ID)
            .configureLogs(LogConfig(LogLevel.VERBOSE, true))
            .configureFcm(FcmConfig(true))
            .setDataCenter(DataCenter.DATA_CENTER_1)
//            .enablePartnerIntegration(IntegrationPartner.SEGMENT)
//            .setDataCenter(DataCenter.DATA_CENTER_4)
            .configureNotificationMetaData(NotificationConfig(R.drawable.ic_hungama_transparent,
                R.drawable.ic_launcher_hungama,
                R.color.teal_200,
            true,
            false,
            true))
//            .configureInApps(InAppConfig())
            .build()

        MoEngage.initialiseDefaultInstance(moEngage)

//        analytics.identify("Segment 32", buildJsonObject {
//            put("name", "Charles")
//            put("Age", 32)
//        })
//
//        analytics.track("View Product", buildJsonObject {
//            put("productId", 123)
//            put("productName", "Striped trousers")
//        });

        MoEAnalyticsHelper.setAppStatus(this, AppStatus.INSTALL)
//        MoEAnalyticsHelper.setAppStatus(this, AppStatus.UPDATE)

        MiPushHelper.initialiseMiPush(App.application!!, appKey, appId, Region.India)

        val pref = getSharedPreferences("APP_INFO", MODE_PRIVATE)
        val versionNo = pref.getInt("CURRENT_APP_VERSION", -1)
        val appVersion = BuildConfig.VERSION_CODE

        if (versionNo == NOT_INSTALLED) {
            // Fresh install
            val edit = pref.edit()
            edit.putInt("CURRENT_APP_VERSION", appVersion).apply();
            MoEAnalyticsHelper.setAppStatus(this, AppStatus.INSTALL)
        } else if (appVersion > versionNo) {
            // Update
            MoEAnalyticsHelper.setAppStatus(this, AppStatus.UPDATE)
        }

        MoECoreHelper.addAppBackgroundListener(object : AppBackgroundListener {
            override fun onAppBackground(context: Context, data: AppBackgroundData) {
                Log.d("MOE_TIMBER", "Going in background")
            }

        })

        MoEPushHelper.getInstance().pushPermissionResponse(App.application!!, true)

//        MoEGeofenceHelper.getInstance().startGeofenceMonitoring(App.application!!)

//        MoEPushHelper.getInstance().setUpNotificationChannels(App.application!!)

        /* Pass the push token everytime the app opens - if empty/null that means the
            app is running for the first time and the token is yet to be generated by the app
         */
//        val token_pref = getSharedPreferences("token_pref", MODE_PRIVATE)
//        val token: String? = token_pref.getString("token", "")
//
//        if(!token.isNullOrBlank())
//        {
//            MoEFireBaseHelper.getInstance().passPushToken(App.application!!, token)
//        }

        /*
            Adding callback for new token add
         */
        MoEFireBaseHelper.getInstance().addTokenListener(object: TokenAvailableListener {
            override fun onTokenAvailable(token: Token) {
                Log.d("MOE_TOKEN_LISTENER", token.pushToken)
            }

        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

//        FirebaseMessaging.getInstance().token?.let {
//            if(it.isSuccessful){
//                val token = it.result
//                token?.let { token ->
//                    Log.d("MOE_TOKEN", token)
//                    MoEFireBaseHelper.getInstance().passPushToken(App.application!!, token)
//                    Log.d("MOE_TOKEN", token)
//                }
//            } else {
//                Log.d("MOE_TOKEN", it.exception?.message.toString())
//            }
//        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Log.d("MOE_TOKEN", it)
            MoEFireBaseHelper.getInstance().passPushToken(App.application!!, it)
        }

//      ----------------

        MoEPushHelper.getInstance().registerMessageListener(MyPushMessageListener())
        MoEInAppHelper.getInstance().showInApp(App.application!!)
//        MoEInAppHelper.getInstance().setSelfHandledListener(object : SelfHandledAvailableListener {
//            override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {
//                Log.e("MOE_MINE_DATA", Gson().toJson(data))
//
//
//                data?.let {
//                    Toast.makeText(App.application, it.campaign.payload, Toast.LENGTH_LONG).show()
//                    val intent = Intent("SHOW_MY_DIALOG")
//                    intent.putExtra("data", Gson().toJson(data))
//                    LocalBroadcastManager.getInstance(this@ApplicationClass).sendBroadcast(intent)
//                    MoEInAppHelper.getInstance().selfHandledShown(App.application!!, it)
////                    var title = "No title provided"
////                    title = data.campaign.payload.toString()
////
////                    val dialog = AlertDialog.Builder(App.application)
////                    dialog.setPositiveButton(
////                        "Yes",
////                        DialogInterface.OnClickListener { dialogInterface, i ->
////                            dialogInterface.dismiss()
////                            MoEInAppHelper.getInstance()
////                                .selfHandledClicked(App.application as Context, data)
////                        })
////                    dialog.setNegativeButton(
////                        "No",
////                        DialogInterface.OnClickListener { dialogInterface, i ->
////                            MoEInAppHelper.getInstance().selfHandledDismissed(App.application as Context, data)
////                            dialogInterface.dismiss()
////                        })
////                    dialog.setTitle(title)
////
////                    dialog.show()
//                }
//            }
//        })


    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event) {
            Lifecycle.Event.ON_START -> {
                Log.d("MOE_LIFECYCLE", "ON START")
                passTokenToSdk()
                MoEAnalyticsHelper.trackEvent(this, "APP_STARTED", Properties())
            }
            Lifecycle.Event.ON_STOP -> {
                MoEAnalyticsHelper.trackEvent(this, "APP_CLOSED", Properties())
            }
            else -> {
                Log.d("MOE_LIFECYCLE", "OTHERS")
            }
        }
    }

    /*
        Pass the push token everytime the app opens - if empty/null that means the
        app is running for the first time and the token is yet to be generated by the app
     */
    private fun passTokenToSdk() {
        val token_pref = getSharedPreferences("token_pref", MODE_PRIVATE)
        val token: String? = token_pref.getString("token", "")

        if(!token.isNullOrBlank())
        {
            MoEFireBaseHelper.getInstance().passPushToken(App.application!!, token)
        }
    }

}