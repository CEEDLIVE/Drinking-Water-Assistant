package hanmo.com.drinkingwaterassistant.tracking

import com.google.android.gms.analytics.HitBuilders
import hanmo.com.drinkingwaterassistant.DWApplication


/**
 * Created by hanmo on 2018. 9. 18..
 */
object GATracker {

    fun setupAppview(screenName: String) {
        try {
            DWApplication.sTracker?.setScreenName(screenName)
            DWApplication.sTracker?.send(HitBuilders.ScreenViewBuilder().build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setupEvent(category : String, action : String) {
        try {
            DWApplication.sTracker?.send(HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setupEvent(category : String, action : String, value : Long) {
        try {
            DWApplication.sTracker?.send(HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setValue(value)
                    .build())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}