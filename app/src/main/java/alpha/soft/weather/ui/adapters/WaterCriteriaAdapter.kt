package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemWaterCriteriaBinding
import alpha.soft.weather.model.CriteriaData
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_water_criteria.view.*

class WaterCriteriaAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<CriteriaData, WaterCriteriaAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemWaterCriteriaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemWaterCriteriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<CriteriaData>() {
        override fun areItemsTheSame(oldItem: CriteriaData, newItem: CriteriaData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CriteriaData, newItem: CriteriaData): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text = it.title
                    tv_subtitle.text= it.category_title
                    tv_count.text = it.izv
                    tv_count.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.color))
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