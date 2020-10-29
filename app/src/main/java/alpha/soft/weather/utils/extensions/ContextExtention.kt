package alpha.soft.weather.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.DisplayMetrics


fun Context.isOnline(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
}

fun Activity.getDeviceWidthInPixels(): Int {
    val display = windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

fun Activity.getDeviceHeightInPixels(): Int {
    val display = windowManager.defaultDisplay
    val outMetrics = DisplayMetrics()
    display.getMetrics(outMetrics)
    return outMetrics.heightPixels
}
