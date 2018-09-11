package hanmo.com.drinkingwaterassistant.lockscreen.background

/**
 * Created by hanmo on 2018. 9. 11..
 */
object BackgroundUtil {

    var imageNameArray = arrayOf("gallery", "image01", "image02")

    fun imageList(): ArrayList<Background> {
        val list = ArrayList<Background>()
        for (i in imageNameArray.indices) {
            var isFav = false
            if (i == 2 || i == 5) {
                isFav = true
            }
            val place = Background(imageNameArray[i].replace("\\s+".toRegex(), "").toLowerCase())
            list.add(place)
        }
        return list
    }
}