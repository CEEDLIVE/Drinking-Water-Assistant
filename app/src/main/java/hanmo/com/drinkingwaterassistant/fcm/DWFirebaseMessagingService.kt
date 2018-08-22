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
import hanmo.com.drinkingwaterassistant.main.MainActivity
import hanmo.com.drinkingwaterassistant.notification.MyNotificationManager
import hanmo.com.drinkingwaterassistant.util.DLog

/**
 * Created by hanmo on 2018. 8. 22..
 */
class DWFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    /**
     * FirebaseInstanceIdService is deprecated.
     * this is new on firebase-messaging:17.1.0
     */
    override fun onNewToken(token: String?) {
        DLog.d("new Token: $token")
    }

    /**
     * this method will be triggered every time there is new FCM Message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        DLog.d("From: " + remoteMessage.from)

        if(remoteMessage.notification != null) {
            DLog.d("Notification Message Body: ${remoteMessage.notification?.body}")
            sendNotification(remoteMessage.notification?.body)
        }
    }


    private fun sendNotification(body: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("Notification", body)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyNotificationManager(this@DWFirebaseMessagingService).createMainNotificationChannel()
        }

        startForeground(DWApplication.notificationId, createNotificationCompatBuilder(body, intent).build())
    }

    private fun createNotificationCompatBuilder(body: String?, intent: Intent): NotificationCompat.Builder {

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //커스텀 화면 만들기
            return NotificationCompat.Builder(this@DWFirebaseMessagingService, MyNotificationManager(this@DWFirebaseMessagingService).getMainNotificationId())
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("Drinking Water Notification")
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent)
        } else {
            return NotificationCompat.Builder(this@DWFirebaseMessagingService,"Notification")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Push Notification FCM")
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(notificationSound)
                    .setContentIntent(pendingIntent)
        }
    }

}