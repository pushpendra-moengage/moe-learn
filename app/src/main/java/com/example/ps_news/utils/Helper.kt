package com.example.ps_news.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ps_news.App
import com.example.ps_news.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class for common functionalities which can be used throughotu the apps
 */
object Helper {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
    private val dateFormatterToString = SimpleDateFormat("dd MMM yyyy h:mm a", Locale.US)

    fun loadImage(iv: ImageView, url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }

        Glide.with(iv).load(url).placeholder(R.drawable.ic_image_placeholder).into(iv)
    }

    fun showToast(msg: String?) {
        App.executors.mainThread().execute {
            Toast.makeText(App.application?.applicationContext, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun getElapsedDateTime(datetime: String?): String {
        //Eg. 2020-02-10T11:40:45Z

        if (datetime.isNullOrEmpty()) {
            return ""
        }

        try {
            val date = dateFormatter.parse(datetime)
            val dateStr = dateFormatterToString.format(date)

            return dateStr

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return datetime
    }

    fun openURLInBrowser(activity: Activity, url: String?) {
        if (url.isNullOrEmpty()) {
            Helper.showToast("Empty or Null URL")
        } else {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            if (browserIntent.resolveActivity(App.application!!.packageManager) != null) {
                activity.startActivity(browserIntent)
            } else {
                Helper.showToast("No browser to open URL")
            }
        }
    }

}