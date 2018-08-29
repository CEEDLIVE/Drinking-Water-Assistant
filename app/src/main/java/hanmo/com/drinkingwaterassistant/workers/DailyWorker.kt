package hanmo.com.drinkingwaterassistant.workers

import androidx.work.Worker
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import io.realm.Realm
import java.util.*

/**
 * Created by hanmo on 2018. 8. 29..
 */
class DailyWorker : Worker() {

    override fun doWork(): Result {
        return if (updateTodayWater()) {
            Worker.Result.SUCCESS
        } else Worker.Result.FAILURE
    }

    private fun updateTodayWater(): Boolean {
        val realm = Realm.getDefaultInstance()
        val todayWaterGoals = realm.where(Goals::class.java).findFirst()
        val todayDateOfCal = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return if (todayWaterGoals?.todayDate != todayDateOfCal) {
            todayWaterGoals?.run {
                realm.executeTransaction {
                    todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    todayMonth = Calendar.getInstance().get(Calendar.MONTH)
                    todayYear = Calendar.getInstance().get(Calendar.YEAR)
                    todayWater = 0
                }
            }

            realm.where(Goals::class.java).findFirst()?.todayDate == todayDateOfCal
        } else {
            false
        }
    }

}