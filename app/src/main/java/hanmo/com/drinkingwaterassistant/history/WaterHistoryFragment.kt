package hanmo.com.drinkingwaterassistant.history

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import hanmo.com.drinkingwaterassistant.DWApplication
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.constans.Const
import kotlinx.android.synthetic.main.fragment_history.view.*


/**
 * Created by hanmo on 2018. 7. 19..
 */
class WaterHistoryFragment : Fragment() {

    private lateinit var mContext : Context

    companion object {
        fun newInstance(): Fragment {
            val args = Bundle()
            //args.putSerializable(dataModel, dataModel as Serializable)
            val fragment = WaterHistoryFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onResume() {
        super.onResume()

        activity?.run {
            mContext = this
        } ?: kotlin.run {
            DWApplication.applicationContext()?.run {
                mContext = this
            } ?: kotlin.run {
                throw IllegalStateException("this application does not Context!!")
            }
        }
        
        val slideDownAnim= AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_list_animation_fall_down)
        view?.historyList?.run {
            layoutAnimation = slideDownAnim
            layoutManager = LinearLayoutManager(mContext)
            val historyAdapter = WaterHistoryAdapter(mContext, RealmHelper.instance.getSortWaterHistory("todayDate"), Const.allHistory)
            adapter = historyAdapter
            setHasFixedSize(true)
        }

    }

}