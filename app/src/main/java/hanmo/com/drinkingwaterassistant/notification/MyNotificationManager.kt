package hanmo.com.drinkingwaterassistant.notification

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi

/**
 * Created by hanmo on 2018. 7. 17..
 */
class MyNotificationManager(private val context : Context) {

    companion object {
        private val CHANNEL_ID = "Water Assistant ID"
        private val CHANNEL_NAME = "Water Assistant Notification CHANEL"
        private val CHANNEL_DESCRIPTION = "Water Assistant CHANEL"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMainNotificationId(): String {
        return CHANNEL_ID
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createMainNotificationChannel() {
        val id = CHANNEL_ID
        val name = CHANNEL_NAME
        val importance = android.app.NotificationManager.IMPORTANCE_HIGH
        val mChannel = NotificationChannel(id, name, importance)

        mChannel.enableVibration(false)
        mChannel.enableLights(false)


        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
    }

}
