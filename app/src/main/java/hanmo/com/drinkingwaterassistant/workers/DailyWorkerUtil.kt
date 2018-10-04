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

    private var onTimeDailyWorker : OneTimeWorkRequest? = null
    private var periodicWorkRequest : PeriodicWorkRequest? = null

    private val workManager: WorkManager by lazy {
        WorkManager.getInstance()
    }

    private val savedMidNightWorkState: LiveData<List<WorkStatus>> by lazy {
        workManager.getStatusesByTag(Const.DAILY_WORKER_TAG)
    }

    private val savedDailyWorkState: LiveData<List<WorkStatus>> by lazy {
        workManager.getStatusesByTag(Const.DAILY_WORKER)
    }

    fun getMidNightWorksState(): LiveData<List<WorkStatus>> = savedMidNightWorkState
    fun getDailyWorksState(): LiveData<List<WorkStatus>> = savedDailyWorkState


    @SuppressLint("SimpleDateFormat")
    private fun getDelayTime(): Long {

        /*val cal = Calendar.getInstance()
            cal?.run {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }*/

        //val midnightTime = cal.timeInMillis / 1000

        val dataFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
        val todayDate = Date(System.currentTimeMillis())
        val currentTime = dataFormat.format(todayDate)

        val d1 = dataFormat.parse("24:00:20")
        val d2 = dataFormat.parse(currentTime)
        val diff = d1.time - d2.time
        val delayTime = diff / 1000

        val dfsdfd = diff / (1000*60)
        DLog.e("WorkManager midnight : ${d1.time / (1000*60)}")
        DLog.e("WorkManager current : ${d2.time / (1000*60)}")
        DLog.e("WorkManager onRequest Delay time : $dfsdfd")


        /*DLog.e("WorkManager midnight : 23:59:59")
        DLog.e("WorkManager current : $currentTime")
        //DLog.e("WorkManager delay : $delayTime")
        DLog.e("WorkManager onRequest Delay time : ${dataFormat.format(Date(diff))}")
        //DLog.e("WorkManager current Times for Delay : ${dataFormat.format(currentTime)}")*/

        return delayTime

    }

    private fun getConstraints(): Constraints {
        return Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()
    }

    fun applyMidnightWorker() {

        onTimeDailyWorker = OneTimeWorkRequest
                .Builder(MidnightAlarmWorker::class.java)
                .setInitialDelay(getDelayTime(), TimeUnit.MICROSECONDS)
                .setConstraints(getConstraints())
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

        periodicWorkRequest = PeriodicWorkRequest
                .Builder(DailyWorker::class.java, 24, TimeUnit.HOURS)
                .addTag(Const.DAILY_WORKER)
                .setConstraints(getConstraints()).build()

        workManager.enqueue(periodicWorkRequest)
    }

    fun cancelMidnightAlarmWorker() {
        onTimeDailyWorker?.run {
            DLog.e("hanmo cancel Midnight Worker !!")
            workManager.cancelWorkById(this.id)
        }
    }

    fun cancelDailyWorker() {
        periodicWorkRequest?.run {
            DLog.e("hanmo cancel Daily Worker !!")
            workManager.cancelWorkById(this.id)
        }
    }

    fun cancelAllWorker() {
        workManager.cancelAllWork()
    }
}