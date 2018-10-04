package hanmo.com.drinkingwaterassistant.anim

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

/**
 * Created by hanmo on 2018. 9. 4..
 */
object AlphaAnim {

    fun startFadeAlphaAnim(textView: View) {
        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        with(alphaAnimation) {
            fillAfter = true
            duration = 500
            textView.startAnimation(this)
        }
    }

    fun startAppearAlphaAnim(textView : View) {
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        textView.visibility = View.VISIBLE
        with(alphaAnimation) {
            fillAfter = true
            duration = 500
            textView.startAnimation(this)
        }
    }
}