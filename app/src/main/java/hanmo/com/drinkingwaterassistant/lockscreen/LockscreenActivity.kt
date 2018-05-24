package hanmo.com.drinkingwaterassistant.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.clicks
import com.triggertrap.seekarc.SeekArc
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.lockscreen.util.UnLock
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_lockscreen.*
import java.util.concurrent.TimeUnit

/**
 * Created by hanmo on 2018. 5. 22..
 */
class LockscreenActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    private val mTimeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            when(intent?.action) {
                Intent.ACTION_TIME_TICK -> {

                }
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lockscreen)

    }


    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        super.onAttachedToWindow()
    }

    override fun onResume() {
        super.onResume()
        compositeDisposable = CompositeDisposable()
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_TIME_TICK)

        registerReceiver(mTimeReceiver, intentFilter)
        setWaterProgress()
        setUnlock()
        setWave()
        setGoals()
        setToday()
        DWApplication.lockScreenShow = true
    }

    private fun setWaterProgress() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        waterProgress.maxProgress = goals?.goal!!
        waterProgress.setCurrentProgress(goals.today!!)


    }

    private fun setToday() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            Const.todayWater = it.today
            val txtGoal = it.today.toString() + "ml"
            todayWater.text = txtGoal

            plusButton.clicks()
                    .debounce(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe {

                        val dd = Const.todayWater?.plus(100)
                        waterProgress.setProgress(Const.todayWater!!, dd!!)

                        Const.todayWater = Const.todayWater?.plus(100)
                        val txtGoall = Const.todayWater.toString() + "ml"
                        todayWater.text = txtGoall
                    }
                    .apply { compositeDisposable.add(this) }
        }
    }

    private fun setGoals() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            val txtGoal = it.goal.toString() + "ml"
            lcGoals.text = txtGoal
        }
    }

    private fun setWave() {
        waveLottie.speed = 1.5f
    }

    private fun setUnlock() {
        lockScreenView.x = 0f
        lockScreenView.setOnTouchListener(object : UnLock(this, lockScreenView) {
            override fun onFinish() {
                finish()
                super.onFinish()
            }

            override fun onTouched() {
                super.onTouched()
            }
        })

    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
        unregisterReceiver(mTimeReceiver)
        RealmHelper.instance.updateTodayWater(Const.todayWater)
        DWApplication.lockScreenShow = false
    }

    override fun onBackPressed() {

    }
}

