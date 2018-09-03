package hanmo.com.drinkingwaterassistant.main

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import hanmo.com.drinkingwaterassistant.DWApplication
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
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.item_water_histroy.view.*

/**
 * Created by hanmo on 2018. 8. 31..
 */
/**
 * 24시가 되면 ToayGoals 값 0으로 바꿔야 한다. 잡스케줄러 사용해서
 * 목표량 넘으면 문구 바꿔서 노출 -값이 아닌 초과하셨어요! 라든지
 */
@SuppressLint("SetTextI18n")
class MainFragment : Fragment() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var waterTable : Goals? = null
    private lateinit var historyAdapter : WaterHistoryAdapter
    private lateinit var mContext : Context

    private val onItemClickListener = object : WaterHistoryAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            historyAdapter.notifyItemRemoved(position)
            historyAdapter.notifyDataSetChanged()
            RealmHelper.instance.deleteHistory(view.historyId.text.toString().toInt())
            RealmHelper.instance.updateTodayWater(view.historyWaterType.text.toString().toInt(), Const.MINUS)
            setProgressBar()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        compositeDisposable = CompositeDisposable()

        /*DailyWorkerUtil.getWorksState().observe(this, Observer {
            if (it == null || it.isEmpty()) return@Observer
            val listOfWorkState = it[0]

            with(listOfWorkState) {
                val isFinished = state.isFinished

            }
        })*/
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }
    override fun onResume() {
        super.onResume()
        DLog.e("Call onResume!!")

        activity?.run {
            mContext = this
        } ?: kotlin.run {
            DWApplication.applicationContext()?.run {
                mContext = this
            } ?: kotlin.run {
                throw IllegalStateException("this application does not Context!!")
            }
        }

        view.todayWaterText.setOnClickListener {
            startActivity(MyTargetWaterActivity.newIntent(activity))

        }

        view.waterHistoryButton.setOnClickListener {
            startActivity(WaterHistoryActivity.newIntent(activity))
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
        val slideDownAnim= AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_list_animation_fall_down)
        with(view.waterList) {
            layoutAnimation = slideDownAnim
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(mContext)
            historyAdapter = WaterHistoryAdapter(mContext, addWaterData, Const.todayHistory)
            historyAdapter.setOnItemClickListener(onItemClickListener)
            adapter = historyAdapter
        }
    }

    fun setProgressBar() {
        waterTable = RealmHelper.instance.getTodayWaterGoal()
        waterTable?.let {
            view.waterGoal.text = it.goalWater?.toString()
            view.todayWater.text = it.todayWater?.toString()
            val percent : Int = (100 * (it.todayWater!!.toDouble() / it.goalWater!!.toDouble())).toInt()
            view.waterPercent.text = "$percent%"
            view.waterProgressbar.max = it.goalWater!!

            val anim = ProgressBarAnimation(view.waterProgressbar, 0f, it.todayWater!!.toFloat())
            anim.duration = 1000
            view.waterProgressbar.startAnimation(anim)
            //waterProgressbar.progress = it.todayWater!!
            view.todayLeftWaterText.text = "목표량까지${it.goalWater!! - it.todayWater!!}ml 남았어요!"
        }
    }

    private fun setSwitch() {
        Lockscreen.instance.init(mContext)
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            view.lockscreenSwitch.isChecked = it.hasLockScreen!!
            if (it.hasLockScreen!!) {
                Lockscreen.instance.active()
            } else {
                Lockscreen.instance.deactivate()
            }
        }
        //rxCompoundButton Switch
        //lockscreenSwitch.checkedChanges().skipInitialValue().subscribe {  }

        view.lockscreenSwitch.setOnCheckedChangeListener({ _, isChecked ->
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
