package alpha.soft.weather.ui.adapters

import alpha.soft.weather.R
import alpha.soft.weather.databinding.ItemAllBinding
import alpha.soft.weather.model.AllItem
import alpha.soft.weather.model.MapData
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_all.view.*

class AllItemAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<AllItem, AllItemAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemAllBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemAllBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<AllItem>() {
        override fun areItemsTheSame(oldAllItem: AllItem, newAllItem: AllItem): Boolean {
            return oldAllItem == newAllItem
        }

        override fun areContentsTheSame(oldAllItem: AllItem, newAllItem: AllItem): Boolean {
            return oldAllItem == newAllItem
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text = "${adapterPosition + 1} ${it.title}"
                    tv_subtitle.text = it.category_title

                    if (it.si == "-" || it.si == "0") {
                        tv_iza.text = context.getString(R.string.text_count)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tv_iza.setTextColor(context.getColor(R.color.colorTextTitle))
                        } else {
                            tv_iza.setTextColor(Color.parseColor("#1E2124"))
                        }
                        tv_iza.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#ffffff"))
                    } else {
                        tv_iza.text = it.si
                        tv_iza.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor(it.color))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            tv_iza.setTextColor(context.getColor(R.color.colorTextWhite))
                        } else {
                            tv_iza.setTextColor(Color.parseColor("#ffffff"))
                        }
                    }
                    setOnClickListener {
                        itemInterface.itemClick(d.id, d.region_id)
                    }
                    bt_location.setOnClickListener {
                        itemInterface.itemLocationClick(
                            d.region_id,
                            MapData(d.id, d.region_id, d.lat.toDouble(), d.lon.toDouble(), d.color)
                        )
                    }
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(id: String, regionId: String)
        fun itemLocationClick(regionId: String, mapData: MapData)
    }
}