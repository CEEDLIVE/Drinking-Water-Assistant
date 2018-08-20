package hanmo.com.drinkingwaterassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper

/**
 * 디바이스를 재시작해도 잠금화면 서비스가 시작되도록 하기위해
 * Created by hanmo on 2018. 5. 23..
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.run {
            if (action == Intent.ACTION_BOOT_COMPLETED) {
                if (RealmHelper.instance.getHasLockScreenBool()) {
                    Lockscreen.instance.init(context)
                    Lockscreen.instance.active()
                }
            }
        }
    }
}