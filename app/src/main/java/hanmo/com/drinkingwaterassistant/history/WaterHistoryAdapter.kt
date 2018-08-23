package hanmo.com.drinkingwaterassistant.history

import android.annotation.SuppressLint
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
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.widget.TextView



/**
 * Created by hanmo on 2018. 7. 18..
 */
class WaterHistoryAdapter(private val waterHistory: RealmResults<WaterHistory>?, private val type : Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            is AllHistoryViewHolder -> {

                holder.bindView(waterHistory?.get(position))
            }
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
                    itemTodayGoal.text = WaterCalculateUtil.totalTodayWater(it.todayDate).toString()
                    /*waterHistory?.forEach {
                        val intMaxSizeTemp = dummyParentDataItems.get(index).getChildDataItems().size()
                        if (intMaxSizeTemp > intMaxNoOfChild) intMaxNoOfChild = intMaxSizeTemp
                    }*/
                    for (indexView in 0 until waterHistory?.size!!) {
                        val textView = TextView(context)
                        textView.id = indexView
                        textView.setPadding(0, 20, 0, 20)
                        textView.gravity = Gravity.CENTER
                        //textView.background = ContextCompat.getDrawable(context, R.drawable.background_sub_module_text)
                        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        itemChildLayout.addView(textView, layoutParams)
                    }
                }
                val noOfChildTextViews = itemChildLayout.childCount
                val noOfChild = waterHistory?.size!!
                if (noOfChild < noOfChildTextViews) {
                    for (index in noOfChild until noOfChildTextViews) {
                        val currentTextView = itemChildLayout.getChildAt(index) as TextView
                        currentTextView.visibility = View.GONE
                    }
                }
                for (textViewIndex in 0 until noOfChild) {
                    val currentTextView = itemChildLayout.getChildAt(textViewIndex) as TextView
                    currentTextView.text = waterHistory[textViewIndex]?.waterType.toString()
                    /*currentTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(mContext, "" + ((TextView) view).getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });*/
                }
            }
        }

        override fun onClick(v: View?) {
            if (v?.itemChildLayout?.visibility == View.VISIBLE) {
                v.itemChildLayout.visibility = View.GONE;
            } else {
                v?.itemChildLayout?.visibility = View.VISIBLE;
            }
        }
    }
}