package hanmo.com.drinkingwaterassistant.workers

import androidx.work.Worker
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import io.realm.Realm
import java.util.*

/**
 * 자정이 되면 첫 한번 동작
 * Created by hanmo on 2018. 8. 29..
 */
class MidnightAlarmWorker : Worker() {
    override fun doWork(): Result {


        DLog.e("WorkManager midnightWorker start!!")
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
            //DLog.e("${realm.where(Goals::class.java).findFirst().toString()}")
            DailyWorkerUtil.applyDailyWorker()
            realm.where(Goals::class.java).findFirst()?.todayDate == todayDateOfCal
        } else {
            false
        }
    }

}