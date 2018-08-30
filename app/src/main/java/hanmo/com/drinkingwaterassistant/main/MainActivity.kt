package hanmo.com.drinkingwaterassistant.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.View
import android.view.animation.AnimationUtils
import com.jakewharton.rxbinding2.widget.checkedChanges
import hanmo.com.drinkingwaterassistant.MyTargetWaterActivity
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.history.WaterHistoryActivity
import hanmo.com.drinkingwaterassistant.history.WaterHistoryAdapter
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.ProgressBarAnimation
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_water_histroy.view.*
import hanmo.com.drinkingwaterassistant.R.layout.activity_main



/**
 * 24시가 되면 ToayGoals 값 0으로 바꿔야 한다. 잡스케줄러 사용해서
 * 목표량 넘으면 문구 바꿔서 노출 -값이 아닌 초과하셨어요! 라든지
 */
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

        /*DailyWorkerUtil.getWorksState().observe(this, Observer {
            if (it == null || it.isEmpty()) return@Observer
            val listOfWorkState = it[0]

            with(listOfWorkState) {
                val isFinished = state.isFinished

            }
        })*/
    }

    override fun onResume() {
        super.onResume()
        DLog.e("Call onResume!!")

        todayWaterText.setOnClickListener {
            startActivity(MyTargetWaterActivity.newIntent(this@MainActivity))

        }

        waterHistoryButton.setOnClickListener {
            startActivity(WaterHistoryActivity.newIntent(this@MainActivity))
        }

        setGlassLottieKeyframe()
        setSwitch()
        setProgressBar()
        setAddWaterList()
    }

    private fun setGlassLottieKeyframe() {


    }

    private fun setAddWaterList() {
        val addWaterData = RealmHelper.instance.todayWaterHistory()
        DLog.e(addWaterData.toString())
        val slideDownAnim= AnimationUtils.loadLayoutAnimation(this@MainActivity, R.anim.layout_list_animation_fall_down)
        with(waterList) {
            layoutAnimation = slideDownAnim
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
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
        //rxCompoundButton Switch
        //lockscreenSwitch.checkedChanges().skipInitialValue().subscribe {  }

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
