package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import kotlinx.android.synthetic.main.item_lockscreen_menu.view.*

/**
 * Created by hanmo on 2018. 6. 6..
 */
class LockScreenMenuAdapter(val menuList: Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var itemClickListener : OnItemClickListener

    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view : View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LcMenuViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is LcMenuViewHolder -> {
                holder.bindView(menuList[position])
            }
        }
    }

    inner class LcMenuViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder (
            LayoutInflater.from(parent.context).inflate(R.layout.item_lockscreen_menu, parent, false)), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView(item: String) {
            with(itemView){
                this.menuTitle.text = item
            }
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(itemView, adapterPosition)
        }
    }


}