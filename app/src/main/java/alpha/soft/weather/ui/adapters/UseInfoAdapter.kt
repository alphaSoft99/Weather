package alpha.soft.weather.ui.adapters

import alpha.soft.weather.R
import alpha.soft.weather.databinding.ItemUseInfoBinding
import alpha.soft.weather.model.NewsData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_use_info.view.*

class UseInfoAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<NewsData, UseInfoAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemUseInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemUseInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<NewsData>() {
        override fun areItemsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text = it.title
                    Glide.with(itemView.context).load(it.url)
                        .apply { placeholder(R.drawable.logo) }.into(iv_content)
                    setOnClickListener {
                        itemInterface.itemClick(d.id)
                    }
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(id: String)
    }

}