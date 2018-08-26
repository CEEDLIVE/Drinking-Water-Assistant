package hanmo.com.drinkingwaterassistant.main

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import hanmo.com.drinkingwaterassistant.MyTargetWaterActivity
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.history.HistoryFragment
import hanmo.com.drinkingwaterassistant.history.WaterHistoryAdapter
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.ProgressBarAnimation
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_water_histroy.view.*

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var waterTable : Goals? = null
    private lateinit var historyAdapter : WaterHistoryAdapter

    private val onItemClickListener = object : WaterHistoryAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            historyAdapter.notifyItemRemoved(position)
            historyAdapter.notifyDataSetChanged()
            RealmHelper.instance.deleteHistory(view.historyId.text.toString().toInt())
            RealmHelper.instance.updateTodayWater(view.historyWaterType.text.toString().toInt(), Const.MINUS)
            setProgressBar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()

    }

    override fun onResume() {
        super.onResume()
        DLog.e("Call onResume!!")

        todayWaterText.setOnClickListener {
            //startActivity(MyTargetWaterActivity.newIntent(this@MainActivity))
            startActivity(Intent(this, HistoryFragment::class.java))
        }

        setSwitch()
        setProgressBar()
        setAddWaterList()
    }

    private fun setAddWaterList() {
        val addWaterData = RealmHelper.instance.todayWaterHistory()
        DLog.e(addWaterData.toString())
        val slideDownAnim= AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_animation_fall_down)
        with(waterList) {
            layoutAnimation = slideDownAnim
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            historyAdapter = WaterHistoryAdapter(this@MainActivity, addWaterData, Const.todayHistory)
            historyAdapter.setOnItemClickListener(onItemClickListener)
            adapter = historyAdapter
        }
    }

    fun setProgressBar() {
        waterTable = RealmHelper.instance.getTodayWaterGoal()
        waterTable?.let {
            waterGoal.text = it.goalWater?.toString()
            todayWater.text = it.todayWater?.toString()
            val percent : Int = (100 * (it.todayWater!!.toDouble() / it.goalWater!!.toDouble())).toInt()
            waterPercent.text = "$percent%"
            waterProgressbar.max = it.goalWater!!

            val anim = ProgressBarAnimation(waterProgressbar, 0f, it.todayWater!!.toFloat())
            anim.duration = 1000
            waterProgressbar.startAnimation(anim)
            //waterProgressbar.progress = it.todayWater!!
            todayLeftWaterText.text = "목표량까지${it.goalWater!! - it.todayWater!!}ml 남았어요!"
        }
    }

    private fun setSwitch() {
        Lockscreen.instance.init(this@MainActivity)
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            lockscreenSwitch.isChecked = it.hasLockScreen!!
            if (it.hasLockScreen!!) {
                Lockscreen.instance.active()
            } else {
                Lockscreen.instance.deactivate()
            }
        }

        lockscreenSwitch.setOnCheckedChangeListener({ _, isChecked ->
            RealmHelper.instance.updateHasLockScreen(isChecked)
            if (isChecked) {
                Lockscreen.instance.active()
            } else {
                Lockscreen.instance.deactivate()
            }
        })
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
