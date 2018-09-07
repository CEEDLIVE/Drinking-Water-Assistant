package hanmo.com.drinkingwaterassistant.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.MyTargetWaterActivity
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.history.WaterHistoryAdapter
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.settings.SettingsFragment
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.FragmentEventsBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.item_water_histroy.view.*
import java.util.concurrent.TimeUnit


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

    companion object {

        fun newInstance(): MainFragment {
            val args = Bundle()
            //args.putSerializable(dataModel, dataModel as Serializable)
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }

    }

    private val onItemClickListener = object : WaterHistoryAdapter.OnItemClickListener {
        override fun onItemClick(view: View, position: Int) {
            historyAdapter.notifyItemRemoved(position)
            historyAdapter.notifyDataSetChanged()
            RealmHelper.instance.deleteHistory(view.historyId.text.toString().toInt())
            RealmHelper.instance.updateTodayWater(view.historyWaterType.text.toString().toInt(), Const.MINUS)
            setProgressBar()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        compositeDisposable = CompositeDisposable()

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        /*DailyWorkerUtil.getWorksState().observe(this, Observer {
            if (it == null || it.isEmpty()) return@Observer
            val listOfWorkState = it[0]

            with(listOfWorkState) {
                val isFinished = state.isFinished

            }
        })*/

        FragmentEventsBus.instance.fragmentEventObservable.subscribe {
            if (it == FragmentEventsBus.ACTION_FRAGMENT_CREATED){

            } else if (it == FragmentEventsBus.ACTION_FRAGMENT_DESTROYED) {

            }

        }

        return rootView
    }

    private fun replaceFragment(fragment: Fragment, tag : String) {
        activity?.run {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.settingContainer, fragment, tag)
                    .commit()
        }
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

        view?.run {
            todayWaterText.setOnClickListener {
                startActivity(MyTargetWaterActivity.newIntent(activity))
            }

        }

        setSwitch()
        setProgressBar()
        setAddWaterList()
        setSettingsButton()
    }

    private fun setSettingsButton() {
        view?.run {
            settingButton.clicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .doOnNext {

                    }
                    .subscribe {
                        replaceFragment(SettingsFragment.newInstance(), "settings")
                    }
                    .apply { compositeDisposable.add(this) }
        }
    }

    private fun setAddWaterList() {
        view?.run {
            val addWaterData = RealmHelper.instance.todayWaterHistory()
            DLog.e(addWaterData.toString())
            val slideDownAnim= AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_list_animation_fall_down)
            with(waterList) {
                layoutAnimation = slideDownAnim
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext)
                historyAdapter = WaterHistoryAdapter(mContext, addWaterData, Const.todayHistory)
                historyAdapter.setOnItemClickListener(onItemClickListener)
                adapter = historyAdapter
            }
        }

    }

    fun setProgressBar() {
        view?.run {
            waterTable = RealmHelper.instance.getTodayWaterGoal()
            waterTable?.let {
                waterGoal.text = it.goalWater?.toString()
                todayWater.text = it.todayWater?.toString()
                val percent : Int = (100 * (it.todayWater!!.toDouble() / it.goalWater!!.toDouble())).toInt()
                percentLoop(0.0, percent.toDouble())
                waterProgressbar.setProgress(it.todayWater!!, it.goalWater!!)
                todayLeftWaterText.text = "${it.goalWater!! - it.todayWater!!}ml"
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
                    view?.waterPercent?.text = percentTxt
                }
            }
            Const.waterPercent = Math.floor(k).toInt()
        }).start()
    }

    private fun setSwitch() {
        view?.run {
            Lockscreen.instance.init(mContext)
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
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
