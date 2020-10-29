package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemMapInfoBinding
import alpha.soft.weather.model.Iza
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_map_info.view.*

class MapInfoAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<Iza, MapInfoAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemMapInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemMapInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<Iza>() {
        override fun areItemsTheSame(oldGraphic: Iza, newGraphic: Iza): Boolean {
            return oldGraphic == newGraphic
        }

        override fun areContentsTheSame(oldGraphic: Iza, newGraphic: Iza): Boolean {
            return oldGraphic == newGraphic
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text = it.name
                    view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.color))
                    setOnClickListener {
                        itemInterface.itemClick(adapterPosition)
                    }
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(pos: Int)
    }

}