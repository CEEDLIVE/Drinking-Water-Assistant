package hanmo.com.drinkingwaterassistant.util

import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hanmo on 2018. 8. 23..
 */
class WaterCalculateUtil {

    companion object {

        fun totalTodayWater(todayDate: Int?): Int {
            var totalTodayWater = 0
            RealmHelper.instance.getTotalTodayWater(todayDate)?.forEach {
                totalTodayWater += it.waterType!!
            }
            return totalTodayWater
        }

        fun formatCurrentTime(addWaterTime: Long?) : String {
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
            return formatter.format(addWaterTime)
        }
    }
}
