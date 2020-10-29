package alpha.soft.weather.ui.base

import alpha.soft.weather.R
import alpha.soft.weather.utils.Constants
import android.app.AlertDialog
import android.content.Intent
import android.provider.Settings
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment(){

    fun showNetworkErrorDialog(subtitle: String){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.error)
            .setMessage(subtitle)
            .setPositiveButton(R.string.again) { _, _ ->
                refreshProgress()
            }
            .setNeutralButton(R.string.settings) { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_WIRELESS_SETTINGS), Constants.Setting)
            }
            .setCancelable(false)
            .show()
    }

    fun showExceptionDialog(subtitle : String){
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.exception)
            .setMessage(subtitle)
            .setPositiveButton(R.string.ok) { _, _ -> }
            .setCancelable(false)
            .show()
    }

    open fun refreshProgress(){}
}