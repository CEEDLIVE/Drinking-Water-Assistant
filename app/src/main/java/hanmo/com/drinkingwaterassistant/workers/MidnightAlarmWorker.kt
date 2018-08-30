package hanmo.com.drinkingwaterassistant.workers

import androidx.work.Worker
import hanmo.com.drinkingwaterassistant.util.DLog

/**
 * 자정이 되면 첫 한번 동작
 * Created by hanmo on 2018. 8. 29..
 */
class MidnightAlarmWorker : Worker() {
    override fun doWork(): Result {

        DLog.e("WorkManager midnightWorker start!!")
        DailyWorkerUtil.applyDailyWorker()

        return Worker.Result.SUCCESS
    }

}