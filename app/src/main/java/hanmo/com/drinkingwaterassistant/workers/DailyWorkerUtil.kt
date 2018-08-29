package hanmo.com.drinkingwaterassistant.workers

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import androidx.work.*
import hanmo.com.drinkingwaterassistant.constans.Const
import androidx.work.WorkManager
import hanmo.com.drinkingwaterassistant.util.DLog
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 매일 자정에 해야하는 Worker Utill
 * Created by hanmo on 2018. 8. 29..
 */
object DailyWorkerUtil {

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance()
    }

    private val savedWorkState: LiveData<List<WorkStatus>> by lazy {
        workManager.getStatusesByTag(Const.DAILY_WORKER_TAG)
    }

    fun getWorksState(): LiveData<List<WorkStatus>> = savedWorkState

    @SuppressLint("SimpleDateFormat")
    private fun getDelayTime(): Long {

        val cal = Calendar.getInstance()
            cal.run {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }

        val delayTime = (cal.timeInMillis - System.currentTimeMillis()) / 1000
        val aaa = cal.timeInMillis / 1000
        val bbb = System.currentTimeMillis() / 1000

        DLog.e("WorkManager midnight : $aaa")
        DLog.e("WorkManager current : $bbb")
        DLog.e("WorkManager delay : $delayTime")

        DLog.e("WorkManager midnight Times for Delay : ${SimpleDateFormat("HH:mm:ss").format(aaa)}")
        DLog.e("WorkManager current Times for Delay : ${SimpleDateFormat("HH:mm:ss").format(bbb)}")
        DLog.e("WorkManager onRequest Delay time : ${SimpleDateFormat("HH:mm:ss").format(delayTime)}")

        return delayTime

    }

    fun applyMidnightWorker() {

        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

        val onTimeDailyWorker = OneTimeWorkRequest
                .Builder(MidnightAlarmWorker::class.java)
                .setInitialDelay(getDelayTime(), TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build()

        val workerContinuation =
                workManager.beginUniqueWork(Const.DAILY_WORKER_TAG,
                        ExistingWorkPolicy.KEEP,
                        onTimeDailyWorker)

        //onTimeDailyWorker 실행 이후에 할 것 But 주기적 job과 일회성 job을 조합할 수 없다.
        //workerContinuation = workerContinuation.then(OneTimeWorkRequest.Builder(Worker::class.java).build())

        workerContinuation.enqueue()
    }

    fun applyDailyWorker() {

        /*PeriodicWorkRequest.Builder(DailyWorker::class.java, 15, TimeUnit.MINUTES).addTag(LocationWork.TAG)
                .setConstraints(constraints).build()
                .also {
                    workerContinuation = workerContinuation.then(it)
                }*/

    }

}