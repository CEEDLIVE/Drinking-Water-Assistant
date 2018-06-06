package hanmo.com.drinkingwaterassistant.lockscreen.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.app.*
import hanmo.com.drinkingwaterassistant.lockscreen.LockscreenActivity
import hanmo.com.drinkingwaterassistant.util.DLog

/**
 * Created by hanmo on 2018. 5. 22..
 */
class LockScreenService : Service() {

    private var mServiceStartId : Int? = null

    private val mLockscreenReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent) {
            context?.let {
                val actionName = intent.action
                when(actionName) {
                    Intent.ACTION_SCREEN_OFF -> { startLockScreenActivity() }
                    else -> {  }
                }
            }
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