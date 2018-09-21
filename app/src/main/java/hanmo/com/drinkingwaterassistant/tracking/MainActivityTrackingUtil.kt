package hanmo.com.drinkingwaterassistant.tracking

import android.os.Bundle
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import hanmo.com.drinkingwaterassistant.DWApplication

/**
 * Created by hanmo on 2018. 9. 18..
 */
object MainActivityTrackingUtil {

    fun clickedAlarmButton() {
        Answers.getInstance().logCustom(CustomEvent("clickedAlarmButton")
                        .putCustomAttribute("clickAlarm", "clicked"))

        val bundle = Bundle()
        bundle.putString("clickAlarm", "clicked")
        DWApplication.firebaseAnalytics?.logEvent("clickedAlarmButton", bundle)

        GATracker.setupAppview("clickedAlarmButton")
        GATracker.setupEvent("clickAlarm", "clicked")
    }

    fun clickedHistoryButton() {
        Answers.getInstance().logCustom(CustomEvent("clickedHistoryButton")
                        .putCustomAttribute("clickHistory", "clicked"))

        val bundle = Bundle()
        bundle.putString("clickHistory", "clicked")
        DWApplication.firebaseAnalytics?.logEvent("clickedHistoryButton", bundle)

        GATracker.setupAppview("clickedHistoryButton")
        GATracker.setupEvent("clickHistory", "clicked")
    }

    fun showMainView() {
        Answers.getInstance().logCustom(CustomEvent("showMainView")
                .putCustomAttribute("showMain", "showed"))

        val bundle = Bundle()
        bundle.putString("showMain", "showed")
        DWApplication.firebaseAnalytics?.logEvent("showMainView", bundle)

        GATracker.setupAppview("showMainView")
        GATracker.setupEvent("showMain", "showed")
    }

    fun showHistoyView() {
        Answers.getInstance().logCustom(CustomEvent("showHistoyView")
                        .putCustomAttribute("showHistory", "showed"))

        val bundle = Bundle()
        bundle.putString("showHistory", "showed")
        DWApplication.firebaseAnalytics?.logEvent("showHistoyView", bundle)

        GATracker.setupAppview("showHistoyView")
        GATracker.setupEvent("showHistory", "showed")
    }

    fun clickedAdView() {
        Answers.getInstance().logCustom(CustomEvent("MainActivityClickedAdMob")
                .putCustomAttribute("MainActivityClickedAdMob", "clicked"))

        val bundle = Bundle()
        bundle.putString("MainActivityClickedAdMob", "clicked")
        DWApplication.firebaseAnalytics?.logEvent("MainActivityClickedAdMob", bundle)

        GATracker.setupAppview("MainActivityClickedAdMob")
        GATracker.setupEvent("MainActivityClickedAdMob", "clicked")
    }
}