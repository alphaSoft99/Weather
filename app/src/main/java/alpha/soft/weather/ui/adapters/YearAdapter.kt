package alpha.soft.weather.ui.adapters

import alpha.soft.weather.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.item_spinner.view.*

class YearAdapter(val data: ArrayList<String>) : BaseAdapter() {

    var listener : ((Int, String)->Unit)? =null

    fun setItemClickListener(f: (Int, String)->Unit){
        listener = f
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView == null) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_spinner, parent, false)
            val viewHolder = ViewHolder(view)
            viewHolder.bind(getItem(position))
            viewHolder.position = position
            view.tag = viewHolder
            return view
        }
        val viewHolder = convertView.tag as ViewHolder
        viewHolder.position = position
        viewHolder.bind(getItem(position))
        return convertView
    }

    override fun getItem(position: Int) = data[position]

    override fun getItemId(position: Int) = 0L

    override fun getCount() = data.size

    inner class ViewHolder(val view: View) {
        var position = 0

        fun bind(data: String) {
            view.apply {
                setOnClickListener {
                    listener?.invoke(position, data)
                }
                title.text = data
            }
        }

    }
}