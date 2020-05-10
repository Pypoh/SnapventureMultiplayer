package com.example.snapventuremultiplayer.utils.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

class InternetCheck {

    companion object {
        private var instance: InternetCheck? = null
        private var mContext: Context? = null

        fun getInstance(context: Context): InternetCheck {
            if (instance == null) {
                this.instance = InternetCheck()
                this.mContext = context
            }

            return instance as InternetCheck
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            mContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
