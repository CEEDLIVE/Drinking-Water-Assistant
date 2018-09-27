package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.lockscreen.service.LockScreenService

/**
 * Created by hanmo on 2018. 5. 22..
 */
object LockScreen {

    val isActive: Boolean
        get() = DWApplication.applicationContext()?.let {
            isMyServiceRunning(LockScreenService::class.java)
        } ?: kotlin.run {
            false
        }

    fun active() {
        DWApplication.applicationContext()?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, LockScreenService::class.java))
            } else {
                startService(Intent(this, LockScreenService::class.java))
            }
        }
    }

    fun deactivate() {
        DWApplication.applicationContext()?.run {
            stopService(Intent(this, LockScreenService::class.java))
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = DWApplication.applicationContext()?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}