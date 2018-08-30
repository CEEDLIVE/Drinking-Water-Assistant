package hanmo.com.drinkingwaterassistant.history

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.AnimationUtils
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.constans.Const
import kotlinx.android.synthetic.main.fragment_history.*


/**
 * Created by hanmo on 2018. 7. 19..
 */
class WaterHistoryActivity : Activity() {

    companion object {
        fun newIntent(context: Context?) : Intent {
            return Intent(context, WaterHistoryActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_history)
    }

    override fun onResume() {
        super.onResume()
        val slideDownAnim= AnimationUtils.loadLayoutAnimation(this@WaterHistoryActivity, R.anim.layout_list_animation_fall_down)
        historyList?.run {
            layoutAnimation = slideDownAnim
            layoutManager = LinearLayoutManager(this@WaterHistoryActivity)
            val historyAdapter = WaterHistoryAdapter(this@WaterHistoryActivity, RealmHelper.instance.getSortWaterHistory("todayDate"), Const.allHistory)
            adapter = historyAdapter
            setHasFixedSize(true)
        }

    }

}