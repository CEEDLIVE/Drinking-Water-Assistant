package hanmo.com.drinkingwaterassistant.lockscreen.background

import android.content.Context

/**
 * Created by hanmo on 2018. 9. 11..
 */
class Background(private val imageName: String) {
    fun getImageResourceId(context: Context): Int {
        return context.resources.getIdentifier(this.imageName, "drawable", context.packageName)
    }
}