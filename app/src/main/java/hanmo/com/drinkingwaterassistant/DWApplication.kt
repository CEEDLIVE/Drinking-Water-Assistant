package hanmo.com.drinkingwaterassistant

import android.support.multidex.MultiDexApplication

/**
 * Created by hanmo on 2018. 5. 22..
 */
class DWApplication : MultiDexApplication() {

    companion object {
        var lockScreenShow = false
    }
}