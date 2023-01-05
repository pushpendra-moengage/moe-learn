package com.example.ps_news.views.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.example.ps_news.views.home.adapters.NewsFeedAdapter
import com.example.ps_news.views.home.models.Article
import com.moengage.core.MoECoreHelper
import com.moengage.core.Properties
import com.moengage.core.analytics.MoEAnalyticsHelper
import com.moengage.core.enableAndroidIdTracking

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
    private lateinit var mainActivityViewModel: MainActivityViewModel
    val callback: FragmentCallback by lazy { context as FragmentCallback }


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
            Toast.makeText(context, "Logging out", Toast.LENGTH_SHORT).show()
//            MoEAnalyticsHelper.trackDeviceLocale(requireContext())
            enableAndroidIdTracking(App.application!!)

        }

    }

    private fun initViews(view: View) {
        rvNewsFeed = view.findViewById(R.id.rv_news_feed)
        tvOldNewsFirst = view.findViewById(R.id.tv_old_news_first)
        tvNewNewsFirst = view.findViewById(R.id.tv_new_news_first)
        pbNewsFeed = view.findViewById(R.id.pb_news_feed)
        btnLogin = view.findViewById(R.id.tv_login)
        btnLogout = view.findViewById(R.id.tv_logout)
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