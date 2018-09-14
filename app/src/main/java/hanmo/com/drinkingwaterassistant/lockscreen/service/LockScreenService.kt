package hanmo.com.drinkingwaterassistant.lockscreen.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.app.*
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.lockscreen.LockscreenActivity
import hanmo.com.drinkingwaterassistant.notification.MyNotificationManager
import hanmo.com.drinkingwaterassistant.util.DLog

/**
 * Created by hanmo on 2018. 5. 22..
 */
class LockScreenService : Service() {

    private var mServiceStartId : Int? = null
    private lateinit var context: Context
    private var telephonyManager: TelephonyManager? = null
    private var isPhoneIdleNum = 4

    private val mLockscreenReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            context?.let {
                val actionName = intent.action
                when(actionName) {
                    Intent.ACTION_SCREEN_OFF -> {
                        if (telephonyManager == null) {
                            telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                            telephonyManager?.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE)
                        }
                        isPhoneIdleNum = telephonyManager?.callState!!
                        if (isPhoneIdleNum == 0) {
                            startLockScreenActivity()
                        }
                    }
                }
            }
        }
    }

    private val phoneListener = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String?) {
            super.onCallStateChanged(state, incomingNumber)
            isPhoneIdleNum = state
        }
    }

    private fun stateReceiver(isStartReceiver : Boolean) {
        if (isStartReceiver) {
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(Intent.ACTION_TIME_TICK)
            registerReceiver(mLockscreenReceiver, filter)
        } else {
            unregisterReceiver(mLockscreenReceiver)
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this@LockScreenService

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            if (mNotificationManager.getNotificationChannel(MyNotificationManager(context).getMainNotificationId()) == null) {
                DLog.e("Lockscreen Service notification is NULL!!")
            } else {
                DLog.e("Lockscreen Service notification is NOT NULL!!")
            }

            startForeground(DWApplication.notificationId, createNotificationCompatBuilder(context).build())
        }
    }

    private fun createNotificationCompatBuilder(context: Context): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /*val intent_noti = Intent(this, MainActivity::class.java)
            intent_noti.action = Intent.ACTION_MAIN
            intent_noti.addCategory(Intent.CATEGORY_HOME)*/

            //val contentIntent = PendingIntent.getActivity(this, 0, intent_noti, PendingIntent.FLAG_UPDATE_CURRENT)

            val mBuilder = NotificationCompat.Builder(this, MyNotificationManager(context).getMainNotificationId())
            return mBuilder
        } else {
            return NotificationCompat.Builder(context, "")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mServiceStartId = startId
        stateReceiver(true)
        val bundleIntent = intent
        bundleIntent?.let {
            //startLockScreenActivity()
            DLog.e("  onStartCommand intent  existed")

        } ?: kotlin.run {
            DLog.e("  onStartCommand intent NOT existed);")
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        stateReceiver(false)
    }

    private fun startLockScreenActivity() {
        val startLockScreenActIntent = Intent(this@LockScreenService, LockscreenActivity::class.java)
        startLockScreenActIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startLockScreenActIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivity(startLockScreenActIntent)
    }
}