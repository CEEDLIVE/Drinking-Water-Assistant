package hanmo.com.drinkingwaterassistant.workers

import androidx.work.Worker
import hanmo.com.drinkingwaterassistant.util.DLog

/**
 * Created by hanmo on 2018. 8. 29..
 */
class MidnightAlarmWorker : Worker() {
    override fun doWork(): Result {

        DLog.e("WorkManager midnightWorker start!!")

        return Worker.Result.SUCCESS
    }

}