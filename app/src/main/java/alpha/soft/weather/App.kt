package alpha.soft.weather

import alpha.soft.weather.di.component.DaggerUserApiComponent
import alpha.soft.weather.di.component.UserApiComponent
import alpha.soft.weather.di.module.UserApiModule
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.multidex.MultiDex
import java.util.*
import javax.inject.Inject

open class App : Application() {

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private lateinit var apiComponent: UserApiComponent

    private var userLanguage: String? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        apiComponent = DaggerUserApiComponent.builder()
            .userApiModule(UserApiModule(this))
            .build()
        apiComponent.inject(this)
    }

    companion object {
        var instance: App? = null
            private set
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        applyLocale(this, preferencesUtil.getUiLocale())
        super.onConfigurationChanged(newConfig)
    }

    fun applyLocale(context: Context) {
        val uiLocale = preferencesUtil.getUiLocale()
        Log.d("TTT", uiLocale)
        applyLocale(context, uiLocale)
    }

    private fun applyLocale(context: Context, language: String) {
        userLanguage = language
        val locale = Locale(userLanguage!!, Constants.COUNTRY_CODE_RU)
        applyLocale(context, locale)

        val appContext = context.applicationContext
        if (context !== appContext) {
            applyLocale(appContext, locale)
        }
    }

    private fun applyLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val config = resources.configuration
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                val list = Locale.forLanguageTag(userLanguage?:"")
                config.setLocale(list)
            }
            else -> config.setLocale(locale)
        }
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    open fun getApiComponent(): UserApiComponent {
        return apiComponent
    }
}