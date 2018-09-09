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
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.lockscreen.util.UnLock
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_lockscreen.*
import java.util.concurrent.TimeUnit
import android.view.animation.AnimationUtils
import hanmo.com.drinkingwaterassistant.lockscreen.util.LockScreenMenuAdapter
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import kotlinx.android.synthetic.main.item_lockscreen_menu.view.*
import org.jetbrains.anko.toast
import java.util.*
import io.realm.Realm
import android.animation.ValueAnimator
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import com.airbnb.lottie.LottieAnimationView
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.util.DLog
import kotlinx.android.synthetic.main.fragment_main.view.*


/**
 * Created by hanmo on 2018. 5. 22..
 */
class LockscreenActivity : AppCompatActivity() {

    private var waterTable : Goals? = null
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

    private val onItemClickListener = object : LockScreenMenuAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            val menu = view.menuTitle.text.toString()
            when (menu) {
                "menu 1" -> {
                    toast("메뉴 1")
                }
                "menu 2" -> {
                    toast("메뉴 2")
                }
                "menu 3" -> {
                    toast("메뉴 3")
                }
                "menu 4" -> {
                    toast("메뉴 4")
                }
            }
            lcMenuList.visibility = View.GONE
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lockscreen)

    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (hasFocus) window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
        }
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
        waterTable = RealmHelper.instance.getTodayWaterGoal()
        setAddButton()
        //setProgressBar()
        setUnlock()
        setTime()
        setMenu()
        DWApplication.lockScreenShow = true
    }

    private fun setAddButton() {
        addWaterButton.clicks()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val addEffect = LottieAnimationView(this@LockscreenActivity)
                    addWaterEffectFrame.addView(addEffect)
                    with(addEffect) {
                        setAnimation("add_effect.json")
                        loop(false)
                        playAnimation()
                    }
                }
                .subscribe {
                    updateProgressBar()
                }.apply { compositeDisposable.add(this) }
    }

    private fun updateProgressBar() {
        val realm = Realm.getDefaultInstance()
        val goals = realm.where(Goals::class.java).findFirst()
        val currentIdNum = realm.where(WaterHistory::class.java).max("id")
        val nextId = when (currentIdNum) {
            null -> { 1 }
            else -> { currentIdNum.toInt() + 1 }
        }

        goals?.apply {

            val addWater = WaterHistory()
            addWater.id = nextId
            addWater.waterType = waterType
            addWater.todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            addWater.todayMonth = Calendar.getInstance().get(Calendar.MONTH)
            addWater.todayYear = Calendar.getInstance().get(Calendar.YEAR)
            addWater.addWaterTime = System.currentTimeMillis()
            addWater.todayWaterGoal = goalWater

            realm.executeTransaction {
                todayWater = todayWater?.plus(waterType!!)
                todayDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                todayMonth = Calendar.getInstance().get(Calendar.MONTH)
                todayYear = Calendar.getInstance().get(Calendar.YEAR)

                realm.copyToRealm(addWater)

                waterTable = realm.where(Goals::class.java).findFirst()
            }
        }
        setProgressBar()
    }

    private fun setProgressBar() {
        waterTable?.let {
            waterTable = RealmHelper.instance.getTodayWaterGoal()
            waterTable?.let {
                val percent : Int = (100 * (it.todayWater!!.toDouble() / it.goalWater!!.toDouble())).toInt()
                percentLoop(0.0, percent.toDouble())
                waterProgressbar.setProgress(it.todayWater!!, it.goalWater!!)
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

    private fun setMenu() {
        lcMenuList.visibility = View.GONE

        menuButton.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    if (lcMenuList.visibility == View.GONE) {
                        lcMenuList.visibility = View.VISIBLE
                        val animator = ValueAnimator.ofFloat(0f, 0.5f)
                        animator.addUpdateListener { animation ->
                            with(menuButton) {
                                speed = 0.5f
                                progress = animation.animatedValue as Float
                            }
                         }
                        animator.start()
                        getMenuList()
                    } else {
                        val animator = ValueAnimator.ofFloat(0.5f, 1f)
                        animator.addUpdateListener { animation ->
                            with(menuButton) {
                                speed = 0.5f
                                progress = animation.animatedValue as Float
                            }
                        }
                        animator.start()
                        lcMenuList.visibility = View.GONE
                    }
                }.apply { compositeDisposable.add(this) }
    }


    private fun getMenuList() {

        val menuList = arrayOf("menu 1", "menu 2", "menu 3", "menu 4")

        val resId = R.anim.layout_animation_fall_down
        val animation = AnimationUtils.loadLayoutAnimation(applicationContext, resId)

        with(lcMenuList) {
            layoutAnimation = animation
            setHasFixedSize(true)
            layoutManager = android.support.v7.widget.LinearLayoutManager(applicationContext)
            val menuAdapter = LockScreenMenuAdapter(menuList)
            menuAdapter.setOnItemClickListener(onItemClickListener)
            adapter = menuAdapter
        }
    }

    private fun setTime() {

        val c = Calendar.getInstance()
        val cMonth = c.get(Calendar.MONTH)
        val month = cMonth + 1
        val day = c.get(Calendar.DAY_OF_MONTH)
        val week = c.get(Calendar.DAY_OF_WEEK)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        if (minute < 10) {
            lockscreenTime.text = "$hour:0$minute"
        } else {
            lockscreenTime.text = "$hour:$minute"
        }

        lockscreenDate.text = "${month}월 ${day}일 ${getWeek(week)}"
    }

    private fun getWeek(week: Int): String {
        var dayOfWeek = ""
        when(week) {
            2 -> { dayOfWeek = resources.getString(R.string.monday) }
            3 -> { dayOfWeek = resources.getString(R.string.tuesday) }
            4 -> { dayOfWeek = resources.getString(R.string.wednesday) }
            5 -> { dayOfWeek = resources.getString(R.string.thursday) }
            6 -> { dayOfWeek = resources.getString(R.string.friday) }
            7 -> { dayOfWeek = resources.getString(R.string.saturday) }
            1 -> { dayOfWeek = resources.getString(R.string.sunday) }
        }

        return dayOfWeek
    }

    private fun setUnlock() {
        lockScreenView.x = 0f
        lockScreenView.setOnTouchListener(object : UnLock(this, lockScreenView) {
            override fun onFinish() {
                //waveView.setProgress(100)
                finish()
                super.onFinish()
            }

            override fun onTouched() {
                lcMenuList.visibility = View.GONE
                super.onTouched()
            }

            override fun onMoved(x: Int) {
                if (x > 0) {
                    waveView.setProgress(x)
                }
                super.onMoved(x)
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
        lcMenuList.visibility = View.GONE
    }
}

