package alpha.soft.weather.service

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.ui.base.MainActivity
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.constraintlayout.widget.Constraints
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private  val preference by lazy {
        PreferencesUtil(baseContext, baseContext.getSharedPreferences("OauthPrefs", MODE_PRIVATE))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(Constraints.TAG, "From: " + remoteMessage.from!!)

        applyLocale()

        if (remoteMessage.data.isNotEmpty()) {
            createNotification(remoteMessage.data)
            Log.d(Constants.TAG, "Message data payload: " + remoteMessage.data)
        }

        if (remoteMessage.notification != null) {
            Log.d(Constants.TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }
    }

    override fun onNewToken(token: String) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(object :
            OnCompleteListener<InstanceIdResult> {
            override fun onComplete(p0: Task<InstanceIdResult>) {
                if (p0.isSuccessful) {
                    Log.d("TTT", "getInstanceFailed ${p0.exception}")
                    return
                }
                Log.d("TTT", "token: " + p0.result!!.token)
            }
        })
    }

    private fun applyLocale() {
        val app = application as App
        app.applyLocale(this)
    }

    private fun createNotification(data: Map<String?, String?>) {


        if (data["version"] != null) {
            preference.setVersionControl(data["version"]?.toInt() ?: 1)
        }

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                Constants.UPDATE_CHANNEL_ID,
                Constants.UPDATE_CHANNEL_NAME,
                importance)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, Constants.UPDATE_CHANNEL_ID)
            .setContentTitle(data["title"])
            .setContentText(data["subtitle"])
            .setSubText(data["body"])
            .setAutoCancel(true)
            .setContentIntent(getIntent(data["type"]?:"new_order"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setWhen(System.currentTimeMillis())
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
            .setStyle(NotificationCompat.BigTextStyle())

        manager.notify(Random.nextInt(), builder!!.build())
    }

    private fun getIntent(type: String): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.NOTIFICATION, type)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
