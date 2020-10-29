package alpha.soft.weather.ui.base

import alpha.soft.weather.R
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.ProgressBar
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext

abstract class BaseActivity : AppCompatActivity(){

    private var progress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress = ProgressBar(this)
    }

    fun showNetworkErrorDialog(subtitle: String){
        AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(subtitle)
            .setPositiveButton(R.string.again) { _, _ ->
                refreshProgress()
            }
            .setNeutralButton(R.string.settings) { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), Constants.Setting)
            }
            .show()
    }

    fun showExceptionDialog(subtitle : String){
        AlertDialog.Builder(this)
            .setTitle(R.string.exception)
            .setMessage(subtitle)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .show()
    }

    fun setFullScreen(){
        val window: Window = window
        val winParams: WindowManager.LayoutParams = window.attributes
        winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        window.attributes = winParams
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun showProgress(){
        progress?.showProgressDialog()
    }

    fun hideProgress(){
        progress?.hideProgressDialog()
    }

    open fun refreshProgress(){}

}