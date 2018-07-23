package hanmo.com.drinkingwaterassistant.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R

/**
 * Created by hanmo on 2018. 7. 19..
 */
class HistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_history, container, false)

        return rootView
    }

}