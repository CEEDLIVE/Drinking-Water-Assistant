package hanmo.com.drinkingwaterassistant.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.realm.RealmHelper
import hanmo.com.drinkingwaterassistant.realm.model.WaterHistory
import io.realm.RealmResults
import kotlinx.android.synthetic.main.item_water_histroy.view.*
import java.text.SimpleDateFormat

/**
 * Created by hanmo on 2018. 7. 18..
 */
class WaterHistortAdapter(val waterHistory: RealmResults<WaterHistory>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //lateinit var itemClickListener : OnItemClickListener

    /*fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        //this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view : View, position: Int)
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LcMenuViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return waterHistory?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LcMenuViewHolder -> {
                holder.bindView(waterHistory?.get(position))
            }
        }
    }

    inner class LcMenuViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder (
            LayoutInflater.from(parent.context).inflate(R.layout.item_water_histroy, parent, false)), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView(waterHistoryData: WaterHistory?) {
            with(itemView) {
                val hourFormat = SimpleDateFormat("hh")
                val minFormat = SimpleDateFormat("mm")

                val hour = hourFormat.format(waterHistoryData?.addWaterTime)
                val min = minFormat.format(waterHistoryData?.addWaterTime)

                this.waterHistoryWater.text = "+ ${waterHistoryData?.waterType!!} ml"
                this.waterHistoryTime.text = "${hour}시 ${min}분"

                this.deleteHistoryButton.setOnClickListener {
                    notifyItemRemoved(adapterPosition)
                    notifyDataSetChanged()
                    RealmHelper.instance.deleteHistory(waterHistoryData.id)
                }
            }
        }

        override fun onClick(v: View?) {
            //itemClickListener.onItemClick(itemView, adapterPosition)
        }
    }
}