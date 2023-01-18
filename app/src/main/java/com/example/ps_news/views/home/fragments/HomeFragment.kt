package com.example.ps_news.views.home.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ps_news.App
import com.example.ps_news.R
import com.example.ps_news.utils.Constants
import com.example.ps_news.views.home.MainActivity
import com.example.ps_news.views.home.MainActivityViewModel
import com.example.ps_news.views.home.NewsListDiffUtil
import com.example.ps_news.views.home.adapters.MyInboxAdapter
import com.example.ps_news.views.home.adapters.NewsFeedAdapter
import com.example.ps_news.views.home.models.Article
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.moengage.core.MoECoreHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.disableAllLogs
import com.moengage.core.enableAndroidIdTracking
import com.moengage.firebase.MoEFireBaseHelper
import com.moengage.inapp.MoEInAppHelper
import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.listeners.OnClickActionListener
import com.moengage.inapp.listeners.SelfHandledAvailableListener
import com.moengage.inapp.model.ClickData
import com.moengage.inapp.model.InAppData
import com.moengage.inapp.model.SelfHandledCampaignData
import com.moengage.inbox.core.MoEInboxHelper
import com.moengage.inbox.core.listener.OnMessagesAvailableListener
import com.moengage.inbox.core.model.InboxData
import com.moengage.inbox.ui.MoEInboxUiHelper
import com.moengage.inbox.ui.listener.OnMessageClickListener
import com.moengage.inbox.ui.model.MessageClickData
import com.moengage.inbox.ui.view.InboxActivity
import com.moengage.inbox.ui.view.InboxFragment
import com.moengage.pushbase.MoEPushHelper
import com.moengage.widgets.NudgeView
import org.json.JSONObject

/**
 * This fragment is responsible for showing the list of news feed within itself.
 * The data is updated via viewmodel and a observer is registered here, so whenever
 * viewmodel data changes, it it notified and reflected in UI here
 */
class HomeFragment : Fragment(), NewsFeedAdapter.AdapterCallback {

    lateinit var rvNewsFeed: RecyclerView
    lateinit var adapter: NewsFeedAdapter
    lateinit var tvOldNewsFirst: TextView
    lateinit var tvNewNewsFirst: TextView
    lateinit var pbNewsFeed: ProgressBar
    lateinit var btnLogin: TextView
    lateinit var btnLogout: TextView
    lateinit var btnToggleNotification: TextView
    lateinit var btnExtra: TextView
    private lateinit var mainActivityViewModel: MainActivityViewModel
    val callback: FragmentCallback by lazy { context as FragmentCallback }
    lateinit var nudge: NudgeView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityViewModel =
            ViewModelProvider(context as MainActivity).get(MainActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initViews(view)
        initListeners()
        attachObserver()

        return view
    }

    /**
     * Here we are attaching the observer for the livedata for list od articles, whenever the livedata changes,
     * the observers are notified and onChanged function will be called for the task
     */
    private fun attachObserver() {
        mainActivityViewModel.articlesList.observe(
            context as MainActivity,
            object : Observer<List<Article>?> {
                override fun onChanged(list: List<Article>?) {
                    if (list != null) {

                        updateRecyclerViewWithDiffUtils(list)

//                        adapter.articleList = list
//                        adapter.notifyDataSetChanged()
                        mainActivityViewModel.networkState.postValue(Constants.SUCCESS_STATE)

                    }
                }
            })

        /**
         * This is the viewmodel for networkstate, mainly for the state of request/response and accordingly how the loader
         * should be affected
         */
        mainActivityViewModel.networkState.observe(context as MainActivity, object : Observer<Int> {
            override fun onChanged(t: Int?) {
                if (t == null) {
                    pbNewsFeed.visibility = View.GONE
                } else {
                    when (t) {
                        Constants.SUCCESS_STATE -> pbNewsFeed.visibility = View.GONE
                        Constants.ERROR_STATE -> pbNewsFeed.visibility = View.GONE
                        Constants.LOADING_STATE -> pbNewsFeed.visibility = View.VISIBLE
                    }
                }
            }

        })
    }

    private fun updateRecyclerViewWithDiffUtils(list: List<Article>) {
        val diffcallback = NewsListDiffUtil(adapter.articleList, list)
        val result = DiffUtil.calculateDiff(diffcallback)

        adapter.articleList.clear()
        adapter.articleList.addAll(list)
        result.dispatchUpdatesTo(adapter)

    }

    /**
     * Click listeners for the buttons and initialising recyclerview with required properties
     */
    private fun initListeners() {
        tvOldNewsFirst.setOnClickListener {
            mainActivityViewModel.sortListByDateTime(false)
            // Add eventTrack for button tap
            val properties = Properties()
            properties.addAttribute("btn_tap_event", true)
                .addAttribute("btn_tap_name", "old_news")

            MoEAnalyticsHelper.trackEvent(requireContext(), "Button tap", properties)
        }

        tvNewNewsFirst.setOnClickListener {
            mainActivityViewModel.sortListByDateTime(true)
            // Add eventTrack for button tap - this will be non interactive
            val properties = Properties()
            properties.addAttribute("btn_tap_event", true)
                .addAttribute("btn_tap_name", "new news")
                .setNonInteractive()

            MoEAnalyticsHelper.trackEvent(requireContext(), "Button tap", properties)
        }

//        adapter = NewsFeedAdapter(mainActivityViewModel.articlesList.value, this)
        adapter = NewsFeedAdapter(this)
        rvNewsFeed.adapter = adapter
        rvNewsFeed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        btnLogin.setOnClickListener {
            MoEAnalyticsHelper.setUniqueId(App.application!!, "123_UNIQUE_USER")
            MoEAnalyticsHelper.setUserName(App.application!!, "APPLE_MAC")
            MoEAnalyticsHelper.setLastName(App.application!!, "APPLE_IPAD")
            MoEAnalyticsHelper.setEmailId(App.application!!, "MAC@CHEEZE.COM")
            MoEAnalyticsHelper.setMobileNumber(App.application!!, "9090909090")
            Toast.makeText(context, "Logging in", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
//            MoECoreHelper.logoutUser(App.application!!)
//            Toast.makeText(context, "Logging out", Toast.LENGTH_SHORT).show()
//            MoEAnalyticsHelper.trackDeviceLocale(requireContext())
//            enableAndroidIdTracking(App.application!!)
            MoECoreHelper.logoutUser(App.application!!)

        }

        btnToggleNotification.setOnClickListener {
//            val pref = (context)?.getSharedPreferences("notification_toggle", MODE_PRIVATE)
//            val switchToggle: Boolean = pref?.getBoolean("show_notification", true) == true
//            pref?.edit()?.putBoolean("show_notification", !switchToggle)?.apply()
//
//            Log.d("MOE_TOGGLE", switchToggle.toString())

//            MoEAnalyticsHelper.trackEvent(context!!, "notify_button_tap", Properties())

//            nudge.initialiseNudgeView(activity!!)

        }

        MoEInboxUiHelper.getInstance().setInboxAdapter(MyInboxAdapter())
        MoEInboxUiHelper.getInstance().setOnMessageClickListener(object : OnMessageClickListener{
            override fun onMessageClick(data: MessageClickData): Boolean {
                MoEInboxHelper.getInstance().trackMessageClicked(App.application!!, data.inboxMessage)
                Log.d("MOE_MINE_LISTENER", data.toString())
                return true
            }

        })

        MoEInboxHelper.getInstance().fetchAllMessagesAsync(App.application!!, object : OnMessagesAvailableListener{
            override fun onMessagesAvailable(inboxData: InboxData?) {
                Log.d("MOE_MINE_INBOX_DATA", inboxData.toString())
            }

        })

        btnExtra.setOnClickListener {
            // Inbox with activity
//            val intent = Intent(activity, InboxActivity::class.java)
//            startActivity(intent)
//            MoEAnalyticsHelper.trackEvent(activity!!, "is_rich_tapped", Properties())

            // Inbox with fragment
//            parentFragmentManager
//                .beginTransaction()
//                .replace(R.id.fragment, InboxFragment())
//                .addToBackStack("Inbox")
//                .commit()

            // Inbox with activity with backtap
//            val intent = Intent(activity, InboxActivity::class.java)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment, SecondFragment())
                .commit()

        }

    }

    private fun initViews(view: View) {
        rvNewsFeed = view.findViewById(R.id.rv_news_feed)
        tvOldNewsFirst = view.findViewById(R.id.tv_old_news_first)
        tvNewNewsFirst = view.findViewById(R.id.tv_new_news_first)
        pbNewsFeed = view.findViewById(R.id.pb_news_feed)
        btnLogin = view.findViewById(R.id.tv_login)
        btnLogout = view.findViewById(R.id.tv_logout)
        btnToggleNotification = view.findViewById(R.id.tv_toggle)
        btnExtra = view.findViewById(R.id.tv_extra)
        nudge = view.findViewById(R.id.nudge)
    }

    override fun onResume() {
        super.onResume()

        MoEInAppHelper.getInstance().setInAppContext(setOf("HomeFragContext"))
        MoEInAppHelper.getInstance().showInApp(context!!)

//        MoEInAppHelper.getInstance().getSelfHandledInApp(context!!, object : SelfHandledAvailableListener {
//            override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {
//                val title = getTitle(data)
//                val title: String? = data?.campaign?.payload
//                App.executors.mainThread().execute {
//                    if (title != null) {
//                        val dialog = createAlert(title, data).create()
//                        MoEInAppHelper.getInstance().selfHandledShown(activity as Context, data)
//                        dialog.show()
//                    }
//                }
//            }
//        })
//        MoEInAppHelper.getInstance().showInApp(context!!)
//        MoEInAppHelper.getInstance().addInAppLifeCycleListener(MyInAppLifecycleCallbackListener())
//        MoEInAppHelper.getInstance().setClickActionListener(MyInAppOnClickListener())


//        nudge.initialiseNudgeView(activity!!)
    }

    override fun onPause() {
        MoEInAppHelper.getInstance().resetInAppContext()
        super.onPause()
    }

    private fun getTitle(data: SelfHandledCampaignData?): Any {
        var title = "No title provided"
        val jsonOjb = JSONObject().getJSONObject(data?.campaign?.payload)
        title = jsonOjb.getString("title")

        if(title.isNullOrBlank())
            title = "No title provided"

        return title
    }

    open class MySelfHandledInAppAvailableListener: SelfHandledAvailableListener{
        override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {

            Log.d("MOE_MINE_INAPP_SELF_HANDLED", data.toString())

            if(data?.campaign?.payload?.contains("title") == true) {
//                val payload = data.campaign.payload
//                val dataObj = JsonParser.parseString(payload)

            }

        }

    }

    public fun createAlert(title: String, data: SelfHandledCampaignData): AlertDialog.Builder {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setPositiveButton("Okay", DialogInterface.OnClickListener { dialogInterface, i ->
            MoEInAppHelper.getInstance().selfHandledClicked(activity as Context, data)
            dialogInterface.dismiss()
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { d, i ->
            MoEInAppHelper.getInstance().selfHandledDismissed(activity as Context, data)
            d.dismiss()
        })

        return builder
    }

    open class MyInAppLifecycleCallbackListener: InAppLifeCycleListener {
        override fun onDismiss(inAppData: InAppData) {
            Log.d("MOE_MINE_INAPP_DISMISS", inAppData.toString())
        }

        override fun onShown(inAppData: InAppData) {
            Log.d("MOE_MINE_INAPP_SHOWN", inAppData.toString())
        }

    }

    open class MyInAppOnClickListener: OnClickActionListener {
        override fun onClick(clickData: ClickData): Boolean {
            Log.d("MOE_MINE_INAPP_CLICK", clickData.toString())
            return true
        }

    }

    /**
     * Function overriding callback method for Adapter
     */
    override fun onNewsClick(url: String?) {
        callback.onNewsClick(url)
    }

    interface FragmentCallback {
        fun onNewsClick(url: String?)
    }
}