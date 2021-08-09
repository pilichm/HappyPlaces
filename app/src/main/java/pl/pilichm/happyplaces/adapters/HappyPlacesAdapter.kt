package pl.pilichm.happyplaces.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_happy_place.view.*
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.models.HappyPlaceModel

class HappyPlacesAdapter(
    private val context: Context,
    private var items: ArrayList<HappyPlaceModel>
    ): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HPViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_happy_place, parent, false)
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]

        if (holder is HPViewHolder){
            holder.itemView.ivPlaceImage.setImageURI(Uri.parse(item.image))
            holder.itemView.tvTitle.text = item.title
            holder.itemView.tvDescription.text = item.description
            holder.itemView.setOnClickListener {
                if (onClickListener!=null){
                    onClickListener!!.onClick(position, item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private class HPViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(position: Int, item: HappyPlaceModel)
    }
}