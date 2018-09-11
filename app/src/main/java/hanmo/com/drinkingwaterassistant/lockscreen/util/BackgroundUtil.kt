package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.content.Context
import hanmo.com.drinkingwaterassistant.lockscreen.Background

/**
 * Created by hanmo on 2018. 9. 11..
 */
object BackgroundUtil {

    var imageNameArray = arrayOf("Bora Bora", "Canada", "Dubai", "Hong Kong", "Iceland", "India", "Kenya", "London", "Switzerland", "Sydney")

    fun imageList(): ArrayList<Background> {
        val list = ArrayList<Background>()
        for (i in imageNameArray.indices) {
            var isFav = false
            if (i == 2 || i == 5) {
                isFav = true
            }
            val place = Background(imageNameArray[i], imageNameArray[i].replace("\\s+".toRegex(), "").toLowerCase(), isFav)
            list.add(place)
        }
        return list
    }
}