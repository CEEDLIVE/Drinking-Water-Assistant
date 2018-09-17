package hanmo.com.drinkingwaterassistant.tracking

import android.os.Bundle
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import hanmo.com.drinkingwaterassistant.DWApplication

/**
 * Created by hanmo on 2018. 9. 17..
 */
object LockScreenTrackingUtil {

    fun turnLockScreenView(turnBool : Boolean?) {
        Answers.getInstance()
                .logCustom(CustomEvent("LockScreenTurnOnOff")
                        .putCustomAttribute("hasLockScreen", turnBool.toString())
                )

        val bundle = Bundle()
        bundle.putString("LockScreenTurnOnOff", turnBool.toString())
        DWApplication.firebaseAnalytics?.logEvent("hasLockScreen", bundle)

        //DWApplication.setupAppview("LockScreenOffType")
        //DWApplication.setupEvent("LockScreenOffType", "LockScreenOffType", offType)
    }
}