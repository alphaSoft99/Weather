package alpha.soft.weather.ui.adapters

import alpha.soft.weather.R
import alpha.soft.weather.databinding.ItemNewsLetterBinding
import alpha.soft.weather.model.NewsLetterData
import alpha.soft.weather.utils.extensions.gone
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_news_letter.view.*

class NewsLetterAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<NewsLetterData, NewsLetterAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemNewsLetterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemNewsLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<NewsLetterData>() {
        override fun areItemsTheSame(oldItem: NewsLetterData, newItem: NewsLetterData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NewsLetterData, newItem: NewsLetterData): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_number.text = "${it.number}"
                    tv_title.text = it.title
                    tv_subtitle.text = it.category_title
                    tv_izv.text = it.izv
                    tv_count.text = it.value
                    if(it.izv == ""){
                        tv_count.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#00FFFFFF"))
                        tv_count.typeface = ResourcesCompat.getFont(this.context, R.font.montserrat_medium)
                        tv_izv.gone()
                        tv_count.setTextColor(ColorStateList.valueOf(Color.parseColor("#000000")))
                    }
                    else {
                        try {
                            tv_count.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor(it.color))
                        } catch (e: Exception) {
                            tv_count.backgroundTintList =
                                ColorStateList.valueOf(Color.parseColor("#00FFFFFF"))
                        }
                    }
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