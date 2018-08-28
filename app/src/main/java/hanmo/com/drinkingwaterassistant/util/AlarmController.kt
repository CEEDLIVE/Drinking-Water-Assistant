package hanmo.com.drinkingwaterassistant.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import hanmo.com.drinkingwaterassistant.constans.RequestCodes
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.receiver.AlarmReceiver
import java.util.*

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

        val sender: PendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.getForegroundService(context, RequestCodes.alarmUpdateCodes,
                            alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                } else {
                    PendingIntent.getService(context, RequestCodes.alarmUpdateCodes,
                            alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }
        val wakeTime = setWakeTime()

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {        // KITKAT and later
                am.setRepeating(AlarmManager.RTC_WAKEUP, wakeTime.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7, sender)
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, wakeTime.timeInMillis, sender)
            }
            context.sendBroadcast(alarmIntent)
        } else {
            val showOperation = PendingIntent.getBroadcast(context, RequestCodes.alarmUpdateCodes,
                    alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            am.setAlarmClock(AlarmManager.AlarmClockInfo(wakeTime.timeInMillis, showOperation), showOperation)
        }
    }

    fun cancelAlarm() {
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, RequestCodes.alarmUpdateCodes, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(sender)
        sender.cancel()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //val intent = Intent("android.intent.action.ALARM_CHANGED")
            //context.sendBroadcast(intent)
        }

    }

    private fun setWakeTime(): Calendar {
        val cal = Calendar.getInstance()
        cal?.run {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            //add(Calendar.DATE, 0)
            set(Calendar.DAY_OF_WEEK, 0)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            /*if (System.currentTimeMillis() >= timeInMillis) {

            }*/
        }
        return cal
    }
}