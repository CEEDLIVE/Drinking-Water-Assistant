package hanmo.com.drinkingwaterassistant.workers

import android.arch.lifecycle.LiveData
import androidx.work.*
import hanmo.com.drinkingwaterassistant.constans.Const
import androidx.work.WorkManager
import hanmo.com.drinkingwaterassistant.util.DLog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by hanmo on 2018. 8. 29..
 */
class DailyWorkerUtil {
    private val workManager: WorkManager by lazy {
        WorkManager.getInstance()
    }

    private val savedWorkState: LiveData<List<WorkStatus>> by lazy {
        workManager.getStatusesByTag(Const.DAILY_WORKER_TAG)
    }

    private fun getDelayTime(): Long {

        val cal = Calendar.getInstance()
            cal?.run {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 990)
            }

        DLog.e("WorkManager midnight Times for Delay : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(cal.timeInMillis)}")
        DLog.e("WorkManager current Times for Delay : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis())}")
        DLog.e("workManager onRequest Delay time : ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(cal.timeInMillis - System.currentTimeMillis())}")

        return cal.timeInMillis - System.currentTimeMillis()

    }

    fun applyBlur() {

        OneTimeWorkRequest.Builder(DailyWorker::class.java).setInitialDelay(getDelayTime(), TimeUnit.MILLISECONDS)


    }

}