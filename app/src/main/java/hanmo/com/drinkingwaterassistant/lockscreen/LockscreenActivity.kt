package hanmo.com.drinkingwaterassistant.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.lockscreen.util.UnLock
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_lockscreen.*
import java.util.concurrent.TimeUnit
import android.os.SystemClock
import android.support.design.widget.FloatingActionButton
import android.widget.ImageView
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.*
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import hanmo.com.drinkingwaterassistant.util.DLog
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import java.util.*


/**
 * Created by hanmo on 2018. 5. 22..
 */
class LockscreenActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable

    private val mTimeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            when(intent?.action) {
                Intent.ACTION_TIME_TICK -> {
                    setTime()
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
        setTime()
        setWaterSetting()
        DWApplication.lockScreenShow = true
    }

    private fun setWaterSetting() {

        val fabIconNew = ImageView(this@LockscreenActivity)
        fabIconNew.setImageDrawable(resources.getDrawable(R.drawable.ic_water_setting))
        val fab = com.oguzdev.circularfloatingactionmenu.library
                .FloatingActionButton.Builder(this@LockscreenActivity)
                .setContentView(fabIconNew).build()

        val itemBuilder = SubActionButton.Builder(this@LockscreenActivity)
        val itemIcon = ImageView(this@LockscreenActivity)
        itemIcon.setImageDrawable(resources.getDrawable(R.drawable.ic_aaaa))
        val menu1 = itemBuilder.setContentView(itemIcon).build()

        val actionMenu = FloatingActionMenu.Builder(this@LockscreenActivity).addSubActionView(menu1).attachTo(fab).build()


    }

    private fun setTime() {

        val c = Calendar.getInstance()
        val cMonth = c.get(Calendar.MONTH)
        val month = cMonth + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val week = c.get(Calendar.DAY_OF_WEEK)
        var hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        if (minute < 10) {
            lockscreenTime.text = "$hour:0$minute"
        } else {
            lockscreenTime.text = "$hour:$minute"
        }

        lockscreenDate.text = "${month}월 ${day}일 ${getWeek(week)}"
    }

    private fun getWeek(week: Int): String {
        var DayOfWeek = ""
        when(week) {
            2 -> { DayOfWeek = resources.getString(R.string.monday) }
            3 -> { DayOfWeek = resources.getString(R.string.tuesday) }
            4 -> { DayOfWeek = resources.getString(R.string.wednesday) }
            5 -> { DayOfWeek = resources.getString(R.string.thursday) }
            6 -> { DayOfWeek = resources.getString(R.string.friday) }
            7 -> { DayOfWeek = resources.getString(R.string.saturday) }
            1 -> { DayOfWeek = resources.getString(R.string.sunday) }
        }

        return DayOfWeek
    }

    private fun setWaterProgress() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            with(waterProgress) {
                setProgress(it.today!!, it.goal!!)
                val percent = 100 * (it.today!!.toDouble() / it.goal!!.toDouble())
                percentLoop(0.0, percent)
            }
        }

    }

    private fun percentLoop(current : Double, percent: Double) {
        val mHandler = Handler()
        var k = current
        Thread(Runnable {
            while (k < percent) {
                k += 1.0
                SystemClock.sleep(10L)
                mHandler.post {
                    val percentTxt = Math.floor(k).toInt().toString() + "%"
                    waterPercent.text = percentTxt
                }
            }
            Const.waterPercent = Math.floor(k).toInt()
        }).start()
    }


    private fun setToday() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            Const.todayWater = it.today!!
            Const.goal = it.goal!!
            val txtGoal = it.today.toString() + "ml"
            todayWater.text = txtGoal

            plusButton.clicks()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext {
                        Const.todayWater = Const.todayWater.plus(100)

                        waterProgress.setProgress(Const.todayWater, Const.goal)
                        val percent = 100 * (Const.todayWater.toDouble() / Const.goal.toDouble())
                        percentLoop(Const.waterPercent.toDouble(), percent)

                        val txtGoall = Const.todayWater.toString() + "ml"
                        todayWater.text = txtGoall
                    }
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribe {
                        val realm = Realm.getDefaultInstance()
                        val goals = realm.where(Goals::class.java).findFirst()
                        goals?.let {
                            realm.executeTransaction {
                                goals.today = Const.todayWater
                            }
                        }
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
        waveLottie.speed = 2.5f
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
        DWApplication.lockScreenShow = false
    }

    override fun onBackPressed() {

    }
}

