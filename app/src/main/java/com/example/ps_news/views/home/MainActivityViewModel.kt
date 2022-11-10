package com.example.ps_news.views.home

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ps_news.App
import com.example.ps_news.utils.Constants
import com.example.ps_news.utils.Helper
import com.example.ps_news.views.home.models.Article
import com.example.ps_news.views.home.models.NewsResponseModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL

class MainActivityViewModel : ViewModel() {

    val articlesList: MutableLiveData<List<Article>?> by lazy {
        MutableLiveData<List<Article>?>()
    }

    val networkState: MutableLiveData<Int> by lazy { MutableLiveData<Int>(1) }

    @WorkerThread
    fun sortListByDateTime(isDescending: Boolean) {
        var newList = articlesList.value

        newList = if (isDescending) {
            newList?.sortedByDescending { it.publishedAt }

        } else {
            newList?.sortedBy {
                it.publishedAt
            }
        }

        articlesList.postValue(newList)
    }

    @WorkerThread
    fun fetchNewsData() {
        networkState.postValue(Constants.LOADING_STATE)

        try {
            val url = URL(Constants.DATA_URL)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 30000

            val code = urlConnection.responseCode

            if (code != 200 && code != 201) {
                Log.e("TAG", "Success : False. Some error occurred")
                throw Exception("Success : False. Some error occurred")
                return
            }

            val rd = BufferedReader(InputStreamReader(urlConnection.inputStream, "UTF-8"))

            var line: String? = null
            val responseData: StringBuilder = StringBuilder()

            try {
                do {
                    line = rd.readLine()
                    if (line != null) {
                        responseData.append(line + '\n')
                    }
                } while (line != null)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val response = App.gson.fromJson<NewsResponseModel>(
                responseData.toString(),
                NewsResponseModel::class.java
            )

//                runOnUiThread {
            if (response != null) {
                response.articles?.let {
                    App.executors.mainThread().execute {
                        articlesList.postValue(it)
                    }

                }
            }
        } catch (e: SocketTimeoutException) {
            Helper.showToast("Connection Timeout :" + e.localizedMessage)
            networkState.postValue(Constants.ERROR_STATE)
        } catch (e: MalformedURLException) {
            Helper.showToast("Malformed URL : " + e.localizedMessage)
            networkState.postValue(Constants.ERROR_STATE)
        } catch (e: IOException) {
            Helper.showToast("IOException : " + e.localizedMessage)
            networkState.postValue(Constants.ERROR_STATE)
        } catch (e: Exception) {
            Helper.showToast("Exception:" + e.localizedMessage)
            networkState.postValue(Constants.ERROR_STATE)
        }


    }
}