package pl.pilichm.happyplaces.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.pilichm.happyplaces.R
import pl.pilichm.happyplaces.activities.AddHappyPlaceActivity
import pl.pilichm.happyplaces.activities.MainActivity
import pl.pilichm.happyplaces.database.DatabaseHandler
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
            val ivPlaceImage = holder.itemView.findViewById(R.id.ivPlaceImage) as ImageView
            val tvTitle = holder.itemView.findViewById(R.id.tvTitle) as TextView
            val tvDescription = holder.itemView.findViewById(R.id.tvDescription) as TextView

            ivPlaceImage.setImageURI(Uri.parse(item.image))
            tvTitle.text = item.title
            tvDescription.text = item.description

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

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int){
        val intent = Intent(context, AddHappyPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACES_DETAILS, items[position])
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun notifyDeleteItem(activity: Activity, position: Int) {
        val dbHandler = DatabaseHandler(activity)
        dbHandler.deleteHappyPlace(items[position])
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    private class HPViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(position: Int, item: HappyPlaceModel)
    }
}