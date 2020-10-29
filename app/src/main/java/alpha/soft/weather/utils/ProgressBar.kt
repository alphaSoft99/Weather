package alpha.soft.weather.utils

import alpha.soft.weather.R
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.WindowManager

class ProgressBar (activity: Activity){
    private var progressDialog: AlertDialog? = null

    init {
        val progressView = LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null)
        val progressBuilder = AlertDialog.Builder(activity)
            .setView(progressView)
            .setCancelable(false)
        progressDialog = progressBuilder.show()
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val displayMetrics = DisplayMetrics()
        activity.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(progressDialog?.window?.attributes)
        val dialogWindowWidth = (displayWidth * 0.5f).toInt()
        val dialogWindowHeight = (displayHeight * 0.2f).toInt()
        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight
        progressDialog?.window?.attributes = layoutParams
        progressDialog?.hide()
    }

    fun showProgressDialog() {
        progressDialog?.show()
    }

    fun hideProgressDialog() {
        progressDialog?.dismiss()
    }
}
