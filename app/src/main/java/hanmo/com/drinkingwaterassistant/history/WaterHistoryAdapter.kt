package hanmo.com.drinkingwaterassistant.history

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.constans.Const
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import hanmo.com.drinkingwaterassistant.util.WaterCalculateUtil
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_history_today_goal.view.*
import kotlinx.android.synthetic.main.item_water_histroy.view.*
import java.text.SimpleDateFormat
import android.widget.LinearLayout
import android.view.Gravity
import android.widget.TextView
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.item_history_today_water.view.*


/**
 * Created by hanmo on 2018. 7. 18..
 */
class WaterHistoryAdapter(private val context : Context, private val waterHistory: RealmResults<WaterHistory>?, private val type : Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var itemClickListener : OnItemClickListener


    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view : View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            Const.todayHistory -> return LcMenuViewHolder(parent)
            Const.allHistory -> return AllHistoryViewHolder(parent)
        }

        throw IllegalArgumentException("ViewType is not valid")
    }

    override fun getItemViewType(position: Int): Int {
        return type
    }

    override fun getItemCount(): Int {
        return waterHistory?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LcMenuViewHolder -> holder.bindView(waterHistory?.get(position))
            is AllHistoryViewHolder -> holder.bindView(waterHistory?.get(position))
        }
    }

    inner class LcMenuViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder (
            LayoutInflater.from(parent.context).inflate(R.layout.item_water_histroy, parent, false)), View.OnClickListener {

        init {
            itemView.deleteHistoryButton.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bindView(waterHistoryData: WaterHistory?) {
            with(itemView) {
                waterHistoryData?.let {
                    val hourFormat = SimpleDateFormat("hh")
                    val minFormat = SimpleDateFormat("mm")

                    val hour = hourFormat.format(it.addWaterTime)
                    val min = minFormat.format(it.addWaterTime)

                    waterHistoryWater.text = "+ ${it.waterType} ml"
                    waterHistoryTime.text = "${hour}시 ${min}분"

                    historyId.text = it.id.toString()
                    historyWaterType.text = it.waterType.toString()
                }
            }
        }
        override fun onClick(v: View?) {
            itemClickListener.onItemClick(itemView, adapterPosition)
        }
    }

    inner class AllHistoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder (
            LayoutInflater.from(parent.context).inflate(R.layout.item_history_today_goal, parent, false)), View.OnClickListener {

        init {
            itemView.itemParentLayout.setOnClickListener(this)
        }

        fun bindView(waterHistoryData: WaterHistory?) {
            with(itemView) {
                itemChildLayout.visibility = View.GONE
                waterHistoryData?.let {
                    itemTodayGoal.text = "${WaterCalculateUtil.totalTodayWater(it.todayDate)}ml"
                    itemTodayGoalDate.text = WaterCalculateUtil.formatDate(it.addWaterTime, true)

                    RealmHelper.instance.getTotalTodayWater(it.todayDate)?.forEach {
                        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        val childView = inflater.inflate(R.layout.item_history_today_water, itemChildLayout, false)
                        childView.itemTodayWater.text = "+ ${it.waterType}ml"
                        childView.itemTodayWaterDate.text = WaterCalculateUtil.formatDate(it.addWaterTime, false)
                        itemChildLayout.addView(childView)
                    }

                }
            }
        }

        override fun onClick(v: View?) {
            if (itemView.itemChildLayout.visibility == View.VISIBLE) {
                itemView.itemChildLayout.visibility = View.GONE
            } else {
                val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down_animation)
                itemView.itemChildLayout.visibility = View.VISIBLE
                itemView.itemChildLayout.startAnimation(slideDown)
            }
        }
    }
}