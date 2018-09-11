package hanmo.com.drinkingwaterassistant.lockscreen.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hanmo.com.drinkingwaterassistant.R
import hanmo.com.drinkingwaterassistant.lockscreen.Background
import kotlinx.android.synthetic.main.item_lockscreen_menu.view.*

/**
 * Created by hanmo on 2018. 6. 6..
 */
class LockScreenMenuAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var itemClickListener : OnItemClickListener

    private val menuList = arrayOf("ic_background", "ic_lockscreen_settings")

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
            is LcMenuViewHolder -> holder.bindView(menuList[position])
        }
    }

    inner class LcMenuViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder (
            LayoutInflater.from(parent.context).inflate(R.layout.item_lockscreen_menu, parent, false)), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        fun bindView(item: String) {
            with(itemView){
                val iconDrawable = Background(item, item).getImageResourceId(context)
                this.menuIcon.setImageResource(iconDrawable)
            }
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(itemView, adapterPosition)
        }
    }


}