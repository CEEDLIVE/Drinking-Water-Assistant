package hanmo.com.drinkingwaterassistant.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import kotlinx.android.synthetic.main.fragment_history.view.*

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
            RealmHelper.instance.getSortWaterHistory("todayMonth")?.run {
                
            }
        }

    }

}