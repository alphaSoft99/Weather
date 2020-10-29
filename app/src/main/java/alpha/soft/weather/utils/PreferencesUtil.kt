package alpha.soft.weather.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class PreferencesUtil(
    private val context: Context,
    private val preferences: SharedPreferences
){
    fun getUiLocale(): String {
        return preferences.getString(Constants.PREFS_UI_LOCALE,
            Constants.LANGUAGE_CODE_RU
        )?: Constants.LANGUAGE_CODE_RU
    }

    @SuppressLint("NewApi")
    fun setUiLocale(locale: String) {
        preferences.edit()
            .putString(Constants.PREFS_UI_LOCALE, locale)
            .apply()
    }

    fun setVersionControl(version: Int) {
        preferences.edit().putInt(Constants.VERSION, version).apply()
    }

    fun getVersionControl(): Int {
        return  preferences.getInt(Constants.VERSION, 1)
    }

    fun firstOpen(): Boolean {
        return preferences.getBoolean(Constants.FIRST_OPEN, true)
    }

    fun setFirstOpen(b: Boolean) {
        preferences.edit().putBoolean(Constants.FIRST_OPEN, b).apply()
    }
}

