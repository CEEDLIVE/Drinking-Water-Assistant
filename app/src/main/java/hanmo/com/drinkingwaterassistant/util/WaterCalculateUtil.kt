package hanmo.com.drinkingwaterassistant.util

import hanmo.com.drinkingwaterassistant.realm.RealmHelper

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
    }
}
