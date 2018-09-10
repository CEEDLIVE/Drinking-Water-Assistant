package hanmo.com.drinkingwaterassistant.util

import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
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

        fun formatDate(addWaterTime: Long?, parent : Boolean) : String {
            return if (parent) {
                val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                formatter.format(addWaterTime)
            } else {
                val hourFormat = SimpleDateFormat("HH", Locale.KOREA)
                val minFormat = SimpleDateFormat("mm", Locale.KOREA)
                "${hourFormat.format(addWaterTime)}${DWApplication.applicationContext()?.getString(R.string.hour)} ${minFormat.format(addWaterTime)}${DWApplication.applicationContext()?.getString(R.string.min)}"
            }

        }
    }
}
