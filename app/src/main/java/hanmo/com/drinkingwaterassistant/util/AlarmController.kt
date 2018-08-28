package hanmo.com.drinkingwaterassistant.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import hanmo.com.drinkingwaterassistant.constans.RequestCodes
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals

/**
 * AlarmManager Util class
 * Created by hanmo on 2018. 8. 28..
 */
class AlarmController(private val context : Context) {

    private val goals : Goals?
        get() {
            return RealmHelper.instance.queryFirst(Goals::class.java)
        }

    fun updateAlarm() {
        val alarmIntent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent: PendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.getForegroundService(context, RequestCodes.alarmUpdateCodes,
                            alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                } else {
                    PendingIntent.getService(context, RequestCodes.alarmUpdateCodes,
                            alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

    }

    fun cancelAlarm() {

    }
}