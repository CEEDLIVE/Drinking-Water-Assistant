package hanmo.com.drinkingwaterassistant.anim

import android.animation.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import android.graphics.Color
import hanmo.com.drinkingwaterassistant.DWApplication


/**
 * Created by hanmo on 2018. 9. 6..
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
object RevealAnimationUtil {

    interface AnimationFinishedListener {
        fun onAnimationFinished()
    }

    fun startColorAnimation(view: View?, startColor: Int, endColor: Int, duration: Int) {
        val anim = ValueAnimator()
        anim.setIntValues(startColor, endColor)
        anim.setEvaluator(ArgbEvaluator())
        anim.addUpdateListener { valueAnimator -> view?.setBackgroundColor(valueAnimator.animatedValue as Int) }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view?.setBackgroundColor(Color.TRANSPARENT)
            }
        })
        anim.duration = duration.toLong()
        anim.start()
    }

    fun registerStartRevealAnimation(view: View?, startColor : Int, endColor : Int, listener: AnimationFinishedListener) {
        view?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                v.removeOnLayoutChangeListener(this)
                val cx = view.right - dpToPx(20)
                val cy = view.height - view.height * 0.4
                val finalRadius = Math.max(view.width, view.height)
                val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy.toInt(), 0F, finalRadius.toFloat()).setDuration(1000L)
                anim.interpolator = FastOutSlowInInterpolator()
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        listener.onAnimationFinished()
                    }
                })
                anim.start()
                startColorAnimation(view, startColor, endColor, 1000)
            }
        })
    }

    fun registerExitRevealAnimation(view: View?, startColor : Int, endColor : Int, listener: AnimationFinishedListener) {
        view?.run {
            val cx = right - dpToPx(20)
            val cy = height - height * 0.4
            val initialRadius = Math.max(width, height)
            val anim = ViewAnimationUtils.createCircularReveal(this, cx, cy.toInt(), initialRadius.toFloat(), 0f).setDuration(1000L)
            anim.interpolator = FastOutSlowInInterpolator()
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    visibility = View.GONE
                    listener.onAnimationFinished()
                }
            })
            anim.start()
            startColorAnimation(this, startColor, endColor, 1000)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val density = DWApplication.applicationContext()?.run {
            resources.displayMetrics.density
        }
        return Math.round(dp.toFloat() * density!!)
    }

}