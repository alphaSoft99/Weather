package alpha.soft.weather.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

class IntentUtil {
    companion object {

        fun openDialer(context: Context?, number: String) {
            try {
                val dialerIntent = Intent(Intent.ACTION_DIAL)
                dialerIntent.data = Uri.parse("tel:$number")
                context?.startActivity(dialerIntent)
            } catch (e: ActivityNotFoundException) {
                // no-op
            }
        }
    }
}