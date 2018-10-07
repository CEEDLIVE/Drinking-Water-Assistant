package hanmo.com.drinkingwaterassistant.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.jakewharton.rxbinding2.view.clicks
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.MyTargetWaterActivity
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.history.WaterHistoryAdapter
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.settings.SettingsFragment
import hanmo.com.drinkingwaterassistant.tracking.MainActivityTrackingUtil
import hanmo.com.drinkingwaterassistant.util.DLog
import hanmo.com.drinkingwaterassistant.util.SettingsFragmentEventsBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_main.view.*
import kotlinx.android.synthetic.main.item_water_histroy.view.*
import java.util.concurrent.TimeUnit


/**
 * 24시가 되면 ToayGoals 값 0으로 바꿔야 한다. 잡스케줄러 사용해서
 * 목표량 넘으면 문구 바꿔서 노출 -값이 아닌 초과하셨어요! 라든지
 * Created by hanmo on 2018. 8. 31..
 */
@SuppressLint("SetTextI18n")
class MainFragment : Fragment() {

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private var waterTable : Goals? = null
    private lateinit var historyAdapter : WaterHistoryAdapter
    private lateinit var mContext : Context

    companion object {
        var possibleDeleteItem = true
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

        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        /*DailyWorkerUtil.getWorksState().observe(this, Observer {
            if (it == null || it.isEmpty()) return@Observer
            val listOfWorkState = it[0]

            with(listOfWorkState) {
                val isFinished = state.isFinished

            }
        })*/
        getSettingsFragmentObserve()

        return rootView
    }

    private fun getSettingsFragmentObserve() {
        SettingsFragmentEventsBus.fragmentEventObservable.subscribe {
            view?.run {
                when (it) {
                    SettingsFragmentEventsBus.ACTION_FRAGMENT_CREATED -> {
                        settingButton.isEnabled = false
                        possibleDeleteItem = false
                        setAddWaterList()
                    }
                    SettingsFragmentEventsBus.ACTION_FRAGMENT_DESTROYED -> {
                        possibleDeleteItem = true
                        settingButton.isEnabled = true
                        myTargetButton.visibility = View.VISIBLE
                        settingButton.visibility = View.VISIBLE
                        waterInfoLayout.visibility = View.VISIBLE
                        waterProgressbarFrame.visibility = View.VISIBLE
                    }
                    SettingsFragmentEventsBus.ACTION_FRAGMENT_START_ANIMATION_FINISHED -> {
                        settingButton.isEnabled = false
                        myTargetButton.visibility = View.GONE
                        settingButton.visibility = View.GONE
                        waterInfoLayout.visibility = View.GONE
                        waterProgressbarFrame.visibility = View.GONE
                    }
                    SettingsFragmentEventsBus.ACTION_FRAGMENT_END_ANIMATION_FINISHED -> {
                        settingButton.isEnabled = true
                        possibleDeleteItem = true
                        setAddWaterList()
                    }
                }
            }
        }.apply { compositeDisposable.add(this) }
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

        MainActivityTrackingUtil.showMainView()

        activity?.run {
            mContext = this
        } ?: kotlin.run {
            DWApplication.applicationContext()?.run {
                mContext = this
            } ?: kotlin.run {
                throw IllegalStateException("this application does not Context!!")
            }
        }

        setMyTargetButton()
        setProgressBar()
        setAddWaterList()
        setSettingsButton()
        initTypeImage()
    }

    private fun initTypeImage() {
        val waterType = RealmHelper.instance.queryFirst(Goals::class.java)?.waterType
        when(waterType) {
            Const.type200 -> {
                view?.waterTypeIcon?.setImageResource(R.drawable.water_type_01)
            }
            Const.type300 -> {
                view?.waterTypeIcon?.setImageResource(R.drawable.water_type_03)
            }
            Const.type500 -> {
                view?.waterTypeIcon?.setImageResource(R.drawable.water_type_02)
            }
        }
    }

    private fun setMyTargetButton() {
        view?.run {
            val myTargetIntent = MyTargetWaterActivity.newIntent(mContext)
            val logoImagePair = Pair.create(myTargetImage as View, "logoImage")
            val typeImagePair = Pair.create(waterTypeIcon as View, "typeImage")
            val targetTextPair = Pair.create(waterGoal as View, "waterText")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, logoImagePair, targetTextPair, typeImagePair)

            myTargetButton.clicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityCompat.startActivity(mContext, myTargetIntent, options.toBundle())
                        } else {
                            startActivity(myTargetIntent)
                        }
                    }
                    .apply { compositeDisposable.add(this) }
        }
    }

    private fun setSettingsButton() {
        view?.settingButton?.run {
            clicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .filter { possibleDeleteItem }
                    .subscribe {
                        SettingsFragmentEventsBus.postFragmentAction(SettingsFragmentEventsBus.ACTION_FRAGMENT_CREATED)
                        replaceFragment(SettingsFragment.newInstance(), "settings")
                    }.apply { compositeDisposable.add(this) }
        }
    }

    private fun setAddWaterList() {
        view?.run {
            val addWaterData = RealmHelper.instance.todayWaterHistory()
            DLog.e(addWaterData.toString())
            with(waterList) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext)
                historyAdapter = WaterHistoryAdapter(mContext, addWaterData, Const.todayHistory)
                if (possibleDeleteItem) {
                    val slideDownAnim= AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_list_animation_fall_down)
                    layoutAnimation = slideDownAnim
                    historyAdapter.setOnItemClickListener(onItemClickListener)
                }
                adapter = historyAdapter
            }
        }

    }

    fun setProgressBar() {
        view?.run {
            waterTable = RealmHelper.instance.getTodayWaterGoal()
            waterTable?.let {
                waterGoal.text = "${it.goalWater}ml"
                todayWater.text = "${it.todayWater}ml"
                val percent : Int = (100 * (it.todayWater!!.toDouble() / it.goalWater!!.toDouble())).toInt()
                percentLoop(0.0, percent.toDouble())
                waterProgressbar.setProgress(it.todayWater!!, it.goalWater!!)
                if (percent < 100) {
                    todayLeftWaterText.text = "${it.goalWater!! - it.todayWater!!}ml"
                    mainLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                } else {
                    mainLayout.setBackgroundResource(R.drawable.gradient_animation_list)
                    val animationDrawable = mainLayout.background as AnimationDrawable
                    with(animationDrawable) {
                        setEnterFadeDuration(3000)
                        setExitFadeDuration(3000)
                        start()
                    }
                    todayLeftWaterText.text = "0ml"
                }
            }
        }

    }

    private fun percentLoop(current : Double, percent: Double) {
        val mHandler = Handler()
        var k = current
        Thread(Runnable {
            if (percent != 0.toDouble()) {
                while (k < percent) {
                    var plusvalue = 1.0
                    when {
                        percent > 100.toDouble()  -> { plusvalue = 5.0 }
                        percent > 200.toDouble()  -> { plusvalue = 20.0 }
                        percent > 300.toDouble()  -> { plusvalue = 30.0 }
                        percent > 400.toDouble()  -> { plusvalue = 50.0 }
                    }
                    k += plusvalue
                    SystemClock.sleep(10L)
                    mHandler.post {
                        val percentTxt = Math.floor(k).toInt().toString() + "%"
                        view?.waterPercent?.text = percentTxt
                    }
                }
                mHandler.post {
                    if (percent != Math.floor(k)) {
                        view?.waterPercent?.text = "${percent.toInt()}%"
                    }
                }
            } else {
                mHandler.post {
                    view?.waterPercent?.text = "0%"
                }

            }
            Const.waterPercent = Math.floor(k).toInt()
        }).start()
    }


    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
