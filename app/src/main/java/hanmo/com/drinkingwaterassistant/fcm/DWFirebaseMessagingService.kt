package hanmo.com.drinkingwaterassistant.fcm

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.MainActivity
import hanmo.com.drinkingwaterassistant.constans.RequestCodes
import hanmo.com.drinkingwaterassistant.notification.MyNotificationManager

/**
 * Created by hanmo on 2018. 8. 22..
 */
class DWFirebaseMessagingService : FirebaseMessagingService() {

    private val context : Context by lazy { this@DWFirebaseMessagingService }

    override fun onNewToken(token: String?) {
        saveDeviceToken(token)
    }

    private fun saveDeviceToken(token: String?) {
        val tokenPref = getSharedPreferences("tokenPref", Context.MODE_PRIVATE)
        tokenPref.edit()?.run {
            putString("deviceToken", token)
            apply()
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data?.run {
            sendNotification(this)
        }
    }


    private fun sendNotification(data: MutableMap<String, String>) {
        val intent = MainActivity.newIntent(context).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("viewName", data["viewName"])
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyNotificationManager(context).createMainNotificationChannel()
        }

        startForeground(DWApplication.notificationId, createNotificationCompatBuilder(data, intent).build())
    }

    private fun createNotificationCompatBuilder(data: MutableMap<String, String>, intent: Intent): NotificationCompat.Builder {

        val pendingIntent = PendingIntent.getActivity(context, RequestCodes.PUSH, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        return NotificationCompat.Builder(context, MyNotificationManager(context).getMainNotificationId())
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setSmallIcon(R.drawable.water_pin_icon)
                .setContentTitle(data["title"])
                .setContentText(data["subTitle"])
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
    }

}