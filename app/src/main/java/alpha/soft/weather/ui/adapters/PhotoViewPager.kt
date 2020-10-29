package alpha.soft.weather.ui.adapters

import alpha.soft.weather.R
import alpha.soft.weather.model.Gallery
import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.viewpager.widget.ViewPager
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

class PhotoViewPager internal constructor(
    internal var context: Context,
    private val imageData: List<Gallery>
) : PagerAdapter() {

    override fun getCount(): Int {
        return imageData.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ImageView
    }

    @SuppressLint("ResourceAsColor")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.setBackgroundResource(R.drawable.logo)
        Glide.with(context).load(imageData[position].img).centerCrop().into(imageView)
        (container as ViewPager).addView(imageView, 0)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as ImageView)
    }
}