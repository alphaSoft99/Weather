package alpha.soft.weather.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.TypedValue
import android.os.StrictMode
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

object Utils {

    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics
        ).roundToInt()
    }

    fun toHourMinuteInterval(msInterval: Long): String {
        val seconds = (msInterval / 1000) % 60
        val minutes = msInterval / 60_000
        return "%02d:%02d".format(minutes, seconds)
    }

    // https://github.com/permissions-dispatcher/PermissionsDispatcher/issues/107
    fun hasPermission(context: Context, permission: String): Boolean {
        return try {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, permission)
        } catch (e: Throwable) {
            false
        }
    }

    fun hasLocationPermission(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    fun strictMode(){
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll()
            .detectNetwork()
            .detectAll()
            .build()
        StrictMode.setThreadPolicy(policy)

    }
}
