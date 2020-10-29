package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemPopulationBinding
import alpha.soft.weather.model.MainData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_population.view.*

class PopulationAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<MainData, PopulationAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemPopulationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemPopulationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<MainData>() {
        override fun areItemsTheSame(
            oldItem: MainData,
            newItem: MainData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: MainData,
            newItem: MainData
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    titleAdapter.text = it.title
                    value_one_adapter.text = it.value_1
                    value_two_adapter.text = it.value_2
                    value_three_adapter.text = it.value_3
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(pos: Int)
    }

}