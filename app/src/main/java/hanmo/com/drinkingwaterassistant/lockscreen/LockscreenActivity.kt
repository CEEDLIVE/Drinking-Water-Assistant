package hanmo.com.drinkingwaterassistant.lockscreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
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
import android.view.animation.AnimationUtils
import android.widget.ImageView
import hanmo.com.drinkingwaterassistant.lockscreen.util.LockScreenMenuAdapter
import hanmo.com.drinkingwaterassistant.util.DLog
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.item_lockscreen_menu.view.*
import org.jetbrains.anko.toast
import java.util.*
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.util.RFABTextUtil.dip2px
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import hanmo.com.drinkingwaterassistant.util.ProgressBarAnimation
import kotlin.collections.ArrayList


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
        waterTable = RealmHelper.instance.queryFirst(Goals::class.java)
        setProgressBar()
        setUnlock()
        setTime()
        setMenu()
        DWApplication.lockScreenShow = true
    }

    private fun setProgressBar() {
        waterTable?.let {
            waterGoal.text = it.goal?.toString()
            todayWater.text = it.today?.toString()
            val percent : Int = (100 * (it.today!!.toDouble() / it.goal!!.toDouble())).toInt()
            waterPercent.text = "$percent%"
            waterProgressbar.max = it.goal!!

            val anim = ProgressBarAnimation(waterProgressbar, 0f, it.today!!.toFloat())
            anim.duration = 1000
            waterProgressbar.startAnimation(anim)

            //waterProgressbar.progress = it.today!!

            todayLeftWaterText.text = "목표량까지${it.goal!! - it.today!!}ml 남았어요!"
        }
    }

    private fun setMenu() {
        lcMenuList.visibility = View.GONE

        menuButton.clicks()
                .throttleFirst(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe {
                    if (lcMenuList.visibility == View.GONE) {
                        lcMenuList.visibility = View.VISIBLE
                        getMenuList()
                    } else {
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

    private fun setUnlock() {
        lockScreenView.x = 0f
        lockScreenView.setOnTouchListener(object : UnLock(this, lockScreenView) {
            override fun onFinish() {
                finish()
                super.onFinish()
            }

            override fun onTouched() {
                lcMenuList.visibility = View.GONE
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
        lcMenuList.visibility = View.GONE
    }
}

