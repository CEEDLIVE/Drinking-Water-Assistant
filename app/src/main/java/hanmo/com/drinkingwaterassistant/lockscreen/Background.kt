package hanmo.com.drinkingwaterassistant.lockscreen

import android.content.Context

/**
 * Created by hanmo on 2018. 9. 11..
 */
class Background(val name: String, private val imageName: String, val isFav: Boolean = false) {
    fun getImageResourceId(context: Context): Int {
        return context.resources.getIdentifier(this.imageName, "drawable", context.packageName)
    }
}