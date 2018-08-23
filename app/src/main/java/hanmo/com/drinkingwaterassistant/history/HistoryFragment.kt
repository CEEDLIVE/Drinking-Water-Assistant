package hanmo.com.drinkingwaterassistant.history

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import kotlinx.android.synthetic.main.fragment_history.view.*
import android.support.v7.widget.SimpleItemAnimator
import hanmo.com.drinkingwaterassistant.constans.Const


/**
 * Created by hanmo on 2018. 7. 19..
 */
class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_history, container, false)
    }

    override fun onResume() {
        super.onResume()

        view.historyList?.run {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            layoutManager = LinearLayoutManager(activity)
            val historyAdapter = WaterHistoryAdapter(RealmHelper.instance.getSortWaterHistory("todayDate"), Const.allHistory)
            adapter = historyAdapter
            setHasFixedSize(true)
        }

    }

}