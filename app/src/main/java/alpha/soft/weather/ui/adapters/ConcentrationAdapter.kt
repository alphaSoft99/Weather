package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemConcentrationsBinding
import alpha.soft.weather.model.Concentration
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_concentrations.view.*

class ConcentrationAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<Concentration, ConcentrationAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemConcentrationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemConcentrationsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<Concentration>() {
        override fun areItemsTheSame(oldConcentration: Concentration, newConcentration: Concentration): Boolean {
            return oldConcentration == newConcentration
        }

        override fun areContentsTheSame(oldConcentration: Concentration, newConcentration: Concentration): Boolean {
            return oldConcentration == newConcentration
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    @Suppress("DEPRECATION")
                    tv_title.text = Html.fromHtml(it.option_value)
                    tv_subtitle.text = it.title_value
                    tv_rating.text = it.k_value
                    tv_rating.setTextColor(Color.parseColor(it.color))
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