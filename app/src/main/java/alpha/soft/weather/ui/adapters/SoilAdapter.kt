package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemSoilBinding
import alpha.soft.weather.model.SoilData
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_soil.view.*
import kotlinx.android.synthetic.main.screen_soil.view.tv_title

class SoilAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<SoilData, SoilAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemSoilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemSoilBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<SoilData>() {
        override fun areItemsTheSame(oldItem: SoilData, newItem: SoilData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SoilData, newItem: SoilData): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text= it.title
                    tv_subtitle.webViewClient = WebViewClient()
                    tv_subtitle.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    tv_subtitle.loadDataWithBaseURL("fake://not/needed", it.content, "text/html", "utf-8", "")
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