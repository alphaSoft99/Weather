package alpha.soft.weather.utils

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utility class for constants
 */
object Constants {

    val SHOW_BUTTON: String = "SHOW_BUTTON"
    val FIRST_OPEN: String = "FIRST_OPEN"
    val REGION_ID: String= "REGION_ID"
    val UZB: String = "uzb"
    val BODY: String = "BODY"
    val NEW_BODY: String = "NEW_BODY"
    val IDS: String = "IDS"
    val TITLE: String = "TITLE"
    val BUNDLE: String = "BUNDLE"
    val REQUEST_PERMISSION: Int = 2
    val GPS_REQUEST: Int = 1
    val Setting: Int = 0
    val PREFS_UI_LOCALE: String = "PREFS_UI_LOCALE"
  //  val BASE_URL: String = "https://atmosfera.its.uz/"
    val BASE_URL: String = "https://monitoring.meteo.uz/"
    val VERSION: String = "VERSION"
    val UPDATE_CHANNEL_NAME: CharSequence = "UPDATE_CHANNEL_NAME_VENDOR"
    val UPDATE_CHANNEL_ID: String = "UPDATE_CHANNEL_ID_VENDOR"
    val NOTIFICATION: String = "NOTIFICATION"
    val TAG: String = "TTT"

    val LANGUAGE_CODE_RU: String = "ru"
    val COUNTRY_CODE_RU: String ="ru"
    
    val NEWWORK_EXCEPTION = listOf(
        UnknownError::class.java,
        UnknownHostException::class.java,
        SocketTimeoutException::class.java,
        ConnectException::class.java,
        IOException::class.java
    )
}
