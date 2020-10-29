package alpha.soft.weather.utils.extensions

import alpha.soft.weather.ui.base.MainActivity
import alpha.soft.weather.ui.base.MainActivityViewModel
import alpha.soft.weather.ui.base.SplashActivity
import alpha.soft.weather.ui.base.SplashActivityViewModel
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

fun Fragment.welcomeViewModel() = ViewModelProviders.of(activitySplash())[SplashActivityViewModel::class.java]
fun Fragment.mainViewModel() = ViewModelProviders.of(activity())[MainActivityViewModel::class.java]

fun Fragment.activity() = activity as MainActivity
fun Fragment.activitySplash() = activity as SplashActivity
