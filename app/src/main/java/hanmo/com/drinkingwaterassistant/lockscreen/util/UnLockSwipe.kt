package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.util.DLog
import kotlinx.android.synthetic.main.view_unlock.view.*

/**
 * Created by hanmo on 2018. 9. 9..
 */
class UnLockSwipe : RelativeLayout {
    private var listenerRight: OnUnlockListener? = null

    private var slideButton: FrameLayout? = null
    private var lockedImage: ImageView? = null


    private var thumbWidth = 0
    private var sliding = false
    private var sliderPosition = 0
    private var initialSliderPosition = 0
    private var initialSlidingX = 0f

    constructor(context: Context) : super(context) {
        init(context, null)

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    fun setOnUnlockListenerRight(listener: OnUnlockListener) {
        this.listenerRight = listener
    }

    fun reset() {
//        lockedImage?.setImageResource(R.drawable.locked)
        lockedImage?.setImageResource(R.drawable.click_water_custom)
        slideButton?.run {
            DLog.e("call reset!")
            val params = layoutParams as RelativeLayout.LayoutParams
            val animator = ValueAnimator.ofInt(params.leftMargin, 0)
            animator.addUpdateListener { valueAnimator ->
                params.leftMargin = valueAnimator.animatedValue as Int
                requestLayout()
            }
            animator.duration = 300
            animator.start()
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_unlock, this, true)

        // Retrieve layout elements
        slideButton = view.swipeButton
        lockedImage = view.lockedImage

        view.swipeButton.visibility = View.VISIBLE
        view.lockedImage.visibility = View.VISIBLE

        // Get padding
        //thumbWidth = dpToPx(120); // 60dp + 2*10dp

        val viewTreeObserver = this.viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    this@UnLockSwipe.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    slideButton?.run {
                        thumbWidth = slideButton?.width!!
                        if (view.width == 0) {
                            view.swipeButton.visibility = View.INVISIBLE
                            view.lockedImage.visibility = View.INVISIBLE
                            init(context, null)
                        }

                        sliderPosition = 0
                        val params = layoutParams as RelativeLayout.LayoutParams
                        params.setMargins(0, 0, 0, 0)
                        layoutParams = params
                    }
                }
            })
        }


    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)


        if (event.action == MotionEvent.ACTION_DOWN) { //126
            if (event.x >= sliderPosition && event.x < sliderPosition + thumbWidth) {
//                lockedImage?.setImageResource(R.drawable.unlocked)
                lockedImage?.setImageResource(R.drawable.click_water_custom)
                sliding = true
                initialSlidingX = event.x
                initialSliderPosition = sliderPosition

            }
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_OUTSIDE) {
            if (sliderPosition >= measuredWidth - (thumbWidth + 20)) {
                if (listenerRight != null) listenerRight?.onUnlock()
            } else {
                sliding = false
                sliderPosition = 0
                reset()
            }
        } else if (event.action == MotionEvent.ACTION_MOVE && sliding) {
            sliderPosition = (initialSliderPosition + (event.x - initialSlidingX)).toInt()
            if (sliderPosition <= 0) {
                sliderPosition = 0
            }

            if (sliderPosition >= measuredWidth - (thumbWidth + 20)) {
                sliderPosition =  measuredWidth - (thumbWidth + 20)
            } else {
                val max = measuredWidth - thumbWidth
                val progress = (sliderPosition * 100 / (max * 1.0f)).toInt()
            }
            setMarginLeft(sliderPosition)
        }

        return true
    }

    private fun setMarginLeft(margin: Int) {
        slideButton?.run {
            val params = layoutParams as RelativeLayout.LayoutParams
            params.setMargins(margin, 0, 0, 0)
            layoutParams = params
        } ?: kotlin.run {
            return
        }

    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    interface OnUnlockListener {
        fun onUnlock()
    }
}
