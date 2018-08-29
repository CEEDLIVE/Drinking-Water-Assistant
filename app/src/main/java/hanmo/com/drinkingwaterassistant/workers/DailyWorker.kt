package hanmo.com.drinkingwaterassistant.workers

import androidx.work.Worker
import hanmo.com.drinkingwaterassistant.realm.RealmHelper

/**
 * Created by hanmo on 2018. 8. 29..
 */
class DailyWorker : Worker() {

    override fun doWork(): Result {
        return if (RealmHelper.instance.updateTodayWaterGoal()) {
            Worker.Result.SUCCESS
        } else Worker.Result.FAILURE
    }
}