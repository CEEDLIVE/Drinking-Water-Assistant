package hanmo.com.drinkingwaterassistant.tracking

import android.os.Bundle
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import hanmo.com.drinkingwaterassistant.DWApplication

/**
 * 잠금화면 Tracking Util
 * Created by hanmo on 2018. 9. 17..
 */
object LockScreenTrackingUtil {

    fun showLockScreenView() {

        Answers.getInstance().logCustom(CustomEvent("showLockScreenView")
                        .putCustomAttribute("showLocksScreen", "LockScreen onCreated Called!"))

        val bundle = Bundle()
        bundle.putString("showLocksScreen", "LockScreen onCreated Called!")
        DWApplication.firebaseAnalytics?.logEvent("showLockScreenView", bundle)

        GATracker.setupAppview("showLockScreenView")
        GATracker.setupEvent("showLocksScreen", "LockScreen onCreated Called!")

    }

    fun turnLockScreenView(turnBool : Boolean?) {
        Answers.getInstance().logCustom(CustomEvent("LockScreenTurnOnOff")
                        .putCustomAttribute("hasLockScreen", turnBool.toString()))

        val bundle = Bundle()
        bundle.putString("hasLockScreen", turnBool.toString())
        DWApplication.firebaseAnalytics?.logEvent("LockScreenTurnOnOff", bundle)

        GATracker.setupAppview("LockScreenOffTypeView")
        GATracker.setupEvent("LockScreenOffType", turnBool.toString())
    }

    fun clickedAdView() {
        Answers.getInstance().logCustom(CustomEvent("LockScreenClickedAdMob")
                .putCustomAttribute("LockScreenClickedAdMob", "clicked"))

        val bundle = Bundle()
        bundle.putString("LockScreenClickedAdMob", "clicked")
        DWApplication.firebaseAnalytics?.logEvent("LockScreenClickedAdMob", bundle)

        GATracker.setupAppview("LockScreenClickedAdMob")
        GATracker.setupEvent("LockScreenClickedAdMob", "clicked")
    }

}