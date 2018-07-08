package hanmo.com.drinkingwaterassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import hanmo.com.drinkingwaterassistant.util.ProgressBarAnimation
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var compositeDisposable: CompositeDisposable
    private var waterTable : Goals? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()
    }

    override fun onResume() {
        super.onResume()

        todayWaterText.setOnClickListener {
            startActivity(MyTargetWaterActivity.newIntent(this@MainActivity))
        }

        waterTable = RealmHelper.instance.queryFirst(Goals::class.java)
        setSwitch()
        setProgressBar()
        setAddWaterList()
    }

    private fun setAddWaterList() {
        val addWaterData = RealmHelper.instance.queryAll(WaterHistory::class.java)
        with(waterList) {
            addWaterData?.forEach {
                
            }
        }
    }

    private fun setProgressBar() {
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
