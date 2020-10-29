package alpha.soft.weather.ui.adapters

import alpha.soft.weather.databinding.ItemPhoneBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_all.view.*

class PhoneAdapter(private val itemInterface: ItemInterface) :
    ListAdapter<String, PhoneAdapter.ViewHolder>(DiffUtilImp) {

    private var _binding: ItemPhoneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = ItemPhoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    object DiffUtilImp : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldString: String, newString: String): Boolean {
            return oldString == newString
        }

        override fun areContentsTheSame(oldString: String, newString: String): Boolean {
            return oldString == newString
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            val d = getItem(adapterPosition)
            d?.let {
                itemView.apply {
                    tv_title.text = it
                    setOnClickListener {
                        itemInterface.itemClick(d)
                    }
                }
            }
        }
    }

    interface ItemInterface{
        fun itemClick(phone: String)
    }
}