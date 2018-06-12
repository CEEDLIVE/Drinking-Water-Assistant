package hanmo.com.drinkingwaterassistant

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hanmo.com.drinkingwaterassistant.lockscreen.util.Lockscreen
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.Goals
import hanmo.com.drinkingwaterassistant.util.DLog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compositeDisposable = CompositeDisposable()

        initView()
        setSwitch()
    }

    private fun initView() {
        val goals = RealmHelper.instance.queryFirst(Goals::class.java)
        goals?.let {
            DLog.e(it.toString())
            it.goal?.let {
                if (it == 0) {
                    val myTargetIntent = MyTargetWaterActivity.newIntent(this@MainActivity)
                    startActivity(myTargetIntent)
                }
           }
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
