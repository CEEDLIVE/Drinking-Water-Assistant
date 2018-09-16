package hanmo.com.drinkingwaterassistant.lockscreen.background

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import hanmo.com.drinkingwaterassistant.R
import kotlinx.android.synthetic.main.item_card_background.view.*

/**
 * Created by hanmo on 2018. 9. 11..
 */
class ChangeBackgroundAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var itemClickListener : OnItemClickListener
    val background = BackgroundUtil.imageNameArray

    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: Array<String>, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BackgroundCardViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return background.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is BackgroundCardViewHolder -> {
                holder.bindView(background[position])
            }
        }
    }

    inner class BackgroundCardViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_card_background, parent, false)), View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemClickListener.onItemClick(background, adapterPosition)
        }

        fun bindView(item: String) {
            with(itemView){
                if (adapterPosition == 0) {

                }
                else {
                    mygallery.visibility = View.INVISIBLE
                    backgroundImage.visibility = View.VISIBLE
                    Glide.with(context).load(Background(item).getImageResourceId(context)).thumbnail(0.1f).apply(RequestOptions().override(180,180)).into(backgroundImage)
                }
            }
        }
    }


}