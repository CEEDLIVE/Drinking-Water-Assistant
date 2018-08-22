package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

/**
 * Created by hanmo on 2018. 5. 22..
 */
open class UnLock(val context: Context, val lockScreenView: ConstraintLayout) : View.OnTouchListener {

    private var firstTouchX = 0f
    private var layoutPrevX = 0f
    private var lastLayoutX = 0f
    private var layoutInPrevX = 0f
    private var isLockOpen = false
    private var touchMoveX = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                firstTouchX = event.x + 5f
                layoutPrevX = lockScreenView.x
                isLockOpen = true

                onTouched()
            }

            MotionEvent.ACTION_MOVE -> {
                if (isLockOpen) {
                    touchMoveX = (event.rawX - firstTouchX).toInt()
                    if (lockScreenView.x >= 0) {
                        lockScreenView.x = (layoutPrevX + touchMoveX).toInt().toFloat()
                        if (lockScreenView.x < 0) {
                            lockScreenView.x = 0f
                        }
                        lastLayoutX = lockScreenView.x
                    }
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_UP -> { // 1
                if (isLockOpen) {
                    lockScreenView.x = lastLayoutX
                    lockScreenView.y = 0f
                    optimizeForground(lastLayoutX)
                }
                isLockOpen = false
                firstTouchX = 0f
                layoutPrevX = 0f
                layoutInPrevX = 0f
                touchMoveX = 0
                lastLayoutX = 0f
            }
            else -> {
            }
        }

        return true
    }


    private fun optimizeForground(forgroundX: Float) {

        val displayMetrics = context.resources.displayMetrics
        val mDeviceWidth = displayMetrics.widthPixels
        val mDevideDeviceWidth = mDeviceWidth / 6

        if (forgroundX < mDevideDeviceWidth) {
            var startPosition = mDevideDeviceWidth
            while (startPosition >= 0) {
                lockScreenView.x = startPosition.toFloat()
                startPosition--
            }
        } else {
            val animation = TranslateAnimation(0f, mDeviceWidth.toFloat(), 0f, 0f)
            animation.duration = 300
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    lockScreenView.x = mDeviceWidth.toFloat()
                    lockScreenView.y = 0f
                    onFinish()
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            lockScreenView.startAnimation(animation)
        }
    }

    open fun onFinish() {

    }

    open fun onTouched() {

    }

}