package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemAboutBinding
import alpha.soft.weather.databinding.ItemCriteriaBinding
import alpha.soft.weather.model.CriteriaData
import alpha.soft.weather.model.RecommendationData
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_about.view.*
import kotlinx.android.synthetic.main.item_criteria.view.*

class AboutAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<RecommendationData, AboutAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemAboutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<RecommendationData>() {
        override fun areItemsTheSame(
            oldItem: RecommendationData,
            newItem: RecommendationData
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RecommendationData,
            newItem: RecommendationData
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title_a.text = it.content_1
                    tv_subtitle_a.text = it.content_2
                    tv_count_a.text = it.title
                    tv_count_a.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.color))
                    //tv_count_a.setTextColor(Color.parseColor(it.color))
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(pos: Int)
    }

}