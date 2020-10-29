package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemSubmenuBinding
import alpha.soft.weather.model.Submenu
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_submenu.view.*

class SubMenuAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<Submenu, SubMenuAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemSubmenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemSubmenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<Submenu>() {
        override fun areItemsTheSame(oldItem: Submenu, newItem: Submenu): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Submenu, newItem: Submenu): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text = it.title
                    setOnClickListener {
                        itemInterface.itemClick(adapterPosition, d.category_id)
                    }
                }
            }
        }
    }

    interface ItemInterface {
        fun itemClick(pos: Int, categoryId: String)
    }

}