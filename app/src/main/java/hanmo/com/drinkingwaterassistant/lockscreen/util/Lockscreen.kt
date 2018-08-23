package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import hanmo.com.drinkingwaterassistant.lockscreen.service.LockScreenService

/**
 * Created by hanmo on 2018. 5. 22..
 */
class Lockscreen {
    private var context: Context? = null

    val isActive: Boolean
        get() = context?.let {
            isMyServiceRunning(LockScreenService::class.java)
        } ?: kotlin.run {
            false
        }

    fun init(context: Context?) {
        this.context = context


    }

    fun active() {
        context?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(context, LockScreenService::class.java))
            } else {
                startService(Intent(context, LockScreenService::class.java))
            }
        }
    }

    fun deactivate() {
        context?.stopService(Intent(context, LockScreenService::class.java))
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    companion object {

        val instance: Lockscreen
            get() {
                var singleton: Lockscreen? = null

                if (singleton == null) {
                    singleton = Lockscreen()

                }
                return singleton
            }
    }


}