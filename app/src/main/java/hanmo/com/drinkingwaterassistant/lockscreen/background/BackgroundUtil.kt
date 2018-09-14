package hanmo.com.drinkingwaterassistant.lockscreen.background

/**
 * Created by hanmo on 2018. 9. 11..
 */
object BackgroundUtil {

    var imageNameArray = arrayOf("gallery", "image1", "image2", "image3"
    , "image4", "image5", "image6", "image7", "image8", "image9", "image10", "image11", "image12", "image13", "image14"
            , "image15", "image16", "image17")

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