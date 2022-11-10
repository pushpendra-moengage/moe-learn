package com.example.ps_news.views.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ps_news.R
import com.example.ps_news.utils.Constants
import com.example.ps_news.views.home.MainActivity
import com.example.ps_news.views.home.MainActivityViewModel
import com.example.ps_news.views.home.adapters.NewsFeedAdapter
import com.example.ps_news.views.home.models.Article

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
                        adapter.articleList = list
                        adapter.notifyDataSetChanged()
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

    /**
     * Click listeners for the buttons and initialising recyclerview with required properties
     */
    private fun initListeners() {
        tvOldNewsFirst.setOnClickListener {
            mainActivityViewModel.sortListByDateTime(false)
        }

        tvNewNewsFirst.setOnClickListener {
            mainActivityViewModel.sortListByDateTime(true)
        }

//        adapter = NewsFeedAdapter(mainActivityViewModel.articlesList.value, this)
        adapter = NewsFeedAdapter(this)
        rvNewsFeed.adapter = adapter
        rvNewsFeed.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    private fun initViews(view: View) {
        rvNewsFeed = view.findViewById(R.id.rv_news_feed)
        tvOldNewsFirst = view.findViewById(R.id.tv_old_news_first)
        tvNewNewsFirst = view.findViewById(R.id.tv_new_news_first)
        pbNewsFeed = view.findViewById(R.id.pb_news_feed)
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