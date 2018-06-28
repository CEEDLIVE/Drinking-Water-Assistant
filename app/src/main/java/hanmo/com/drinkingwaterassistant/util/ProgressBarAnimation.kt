package hanmo.com.drinkingwaterassistant.util

import android.widget.ProgressBar
import android.view.animation.Animation
import android.view.animation.Transformation


/**
 * Created by hanmo on 2018. 6. 28..
 */
class ProgressBarAnimation(private val progressBar: ProgressBar, private val from: Float, private val to: Float) : Animation() {

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        val value = from + (to - from) * interpolatedTime
        progressBar.progress = value.toInt()
    }

}