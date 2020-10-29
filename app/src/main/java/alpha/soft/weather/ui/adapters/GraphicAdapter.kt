package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemGraphicBinding
import alpha.soft.weather.model.Graphic
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_graphic.view.*

class GraphicAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<Graphic, GraphicAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemGraphicBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemGraphicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<Graphic>() {
        override fun areItemsTheSame(oldGraphic: Graphic, newGraphic: Graphic): Boolean {
            return oldGraphic == newGraphic
        }

        override fun areContentsTheSame(oldGraphic: Graphic, newGraphic: Graphic): Boolean {
            return oldGraphic == newGraphic
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_date.text = it.date
                    tv_si.text = it.si
                    tv_si.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.color_si))
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