package hanmo.com.drinkingwaterassistant.anim

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation

/**
 * Created by hanmo on 2018. 9. 4..
 */
object AlphaAnim {
    fun startFadeAlphaAnim(textView: View) {
        val alphaAnimation = AlphaAnimation(1.0f, 0.1f)
        with(alphaAnimation) {
            textView.startAnimation(alphaAnimation)
            duration = 600
            //fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    textView.visibility = View.GONE
                }
                override fun onAnimationStart(animation: Animation?) {
                    textView.alpha = 0.0f
                }
            })
        }
    }

    fun startAppearAlphaAnim(textView : View) {
        val alphaAnimation = AlphaAnimation(0.2f, 1.0f)
        textView.visibility = View.VISIBLE
        textView.alpha = 0.1f
        with(alphaAnimation) {
            textView.startAnimation(alphaAnimation)
            duration = 600
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    textView.visibility = View.VISIBLE
                    textView.alpha = 1.0f
                }
                override fun onAnimationStart(animation: Animation?) {

                }
            })
        }

    }
}