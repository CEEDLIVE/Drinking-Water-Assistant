package hanmo.com.drinkingwaterassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals

/**
 *
 * Created by hanmo on 2018. 5. 23..
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val lockScreenBool = RealmHelper.instance.queryFirst(Goals::class.java)
            if (lockScreenBool?.hasLockScreen!!) {
                Lockscreen.instance.init(context)
                Lockscreen.instance.active()
            }
        }
    }
}