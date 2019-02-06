package com.testborntocreate.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}

fun noInternetConnection(context: Context) {
    Toast.makeText(context, "No internet connection, please try again later", Toast.LENGTH_SHORT).show()
}