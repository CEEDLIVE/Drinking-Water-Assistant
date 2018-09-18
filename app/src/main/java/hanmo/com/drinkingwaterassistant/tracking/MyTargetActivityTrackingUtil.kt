package hanmo.com.drinkingwaterassistant.tracking

import android.os.Bundle
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import hanmo.com.drinkingwaterassistant.DWApplication

/**
 * Created by hanmo on 2018. 9. 18..
 */
object MyTargetActivityTrackingUtil {

    fun setWaterGoal(waterGoal : String) {
        Answers.getInstance().logCustom(CustomEvent("setWaterGoal")
                .putCustomAttribute("setWaterGoal", waterGoal))

        val bundle = Bundle()
        bundle.putString("setWaterGoal", waterGoal)
        DWApplication.firebaseAnalytics?.logEvent("setWaterGoal", bundle)

        GATracker.setupAppview("setWaterGoal")
        GATracker.setupEvent("setWaterGoal", waterGoal)
    }

    fun setWaterType(waterType : String) {
        Answers.getInstance().logCustom(CustomEvent("setWaterType")
                .putCustomAttribute("setWaterType", waterType))

        val bundle = Bundle()
        bundle.putString("setWaterType", waterType)
        DWApplication.firebaseAnalytics?.logEvent("setWaterType", bundle)

        GATracker.setupAppview("setWaterType")
        GATracker.setupEvent("setWaterType", waterType)
    }

    fun clickedCustomWaterTypeButton() {
        Answers.getInstance().logCustom(CustomEvent("clickedCustomWaterTypeButton")
                .putCustomAttribute("clickCustom", "clicked"))

        val bundle = Bundle()
        bundle.putString("clickCustom", "clicked")
        DWApplication.firebaseAnalytics?.logEvent("clickedCustomWaterTypeButton", bundle)

        GATracker.setupAppview("clickedCustomWaterTypeButton")
        GATracker.setupEvent("clickCustom", "clicked")
    }
}