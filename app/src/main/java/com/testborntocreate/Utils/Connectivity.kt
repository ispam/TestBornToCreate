package com.testborntocreate.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.testborntocreate.R

fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}

fun connectivyManager(function1: () -> Unit, function2: () -> Unit, context: Context) {
    if (isOnline(context)) {
        function1.invoke()
    } else {
        function2.invoke()
    }
}

fun noInternetConnection(context: Context) {
    Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
}