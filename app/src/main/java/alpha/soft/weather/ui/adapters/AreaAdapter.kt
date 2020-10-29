package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemAreaBinding
import alpha.soft.weather.model.AllItem
import alpha.soft.weather.model.AreaData
import alpha.soft.weather.model.MapData
import alpha.soft.weather.utils.extensions.clearAndAddAll
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_area.view.*

class AreaAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<AreaData, AreaAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemAreaBinding? = null
    private val binding get() = _binding!!
    private val itemData = ArrayList<AllItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemAreaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    fun setAllItemData(data: List<AllItem>) {
        itemData.clearAndAddAll(data)
    }


    object DiffUtilImp : DiffUtil.ItemCallback<AreaData>() {
        override fun areItemsTheSame(oldItem: AreaData, newItem: AreaData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AreaData, newItem: AreaData): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {

            val d = getItem(adapterPosition)
            val adapter = AllItemAdapter(object : AllItemAdapter.ItemInterface {
                override fun itemClick(id: String, regionId: String) {
                    itemInterface.itemChildClick(regionId, id)
                }

                override fun itemLocationClick(regionId: String, mapData: MapData) {
                    itemInterface.itemChildLocationClick(regionId, mapData)
                }
            })
            d?.let {
                itemView.apply {
                    tv_title.text = it.title
                    setOnClickListener {
                        expendable.toggle()
                    }
                    bt_icon.setOnClickListener {
                        itemInterface.itemClick(d.id)
                    }
                    rv_point.adapter = adapter
                    rv_point.layoutManager = LinearLayoutManager(context)
                    val data = ArrayList<AllItem>()
                    itemData.forEach { d ->
                        if (d.region_id == it.id)
                            data.add(d)
                    }
                    adapter.submitList(data)
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(regionId: String)
        fun itemChildClick(regionId: String, id: String)
        fun itemChildLocationClick(regionId: String, mapData: MapData)
    }

}