package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemMenuBinding
import alpha.soft.weather.model.Menu
import alpha.soft.weather.model.Submenu
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_menu.view.*

class MenuAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<Menu, MenuAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<Menu>() {
        override fun areItemsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Menu, newItem: Menu): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {

            val d = getItem(adapterPosition)

            val adapter = SubMenuAdapter(object : SubMenuAdapter.ItemInterface {
                override fun itemClick(pos: Int, categoryId: String) {
                    itemView.apply {
                        expendable.toggle()
                        iv_arrow.animate().setDuration(500).rotationBy(180f).start()
                    }
                    itemInterface.itemClick(pos, categoryId, d.submenu[pos])
                }
            })

            d?.let {
                itemView.apply {
                    rv_submenu.layoutManager = LinearLayoutManager(itemView.context)
                    rv_submenu.adapter = adapter
                    adapter.submitList(it.submenu)
                    tv_title.text = it.title
                    setOnClickListener {
                        expendable.toggle()
                        iv_arrow.animate().setDuration(500).rotationBy(180f).start()
                    }
                }
            }

        }
    }

    interface ItemInterface {
        fun itemClick(pos: Int, categoryId: String, submenu: Submenu)
    }

}