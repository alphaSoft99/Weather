package alpha.soft.weather.utils.extensions

import alpha.soft.weather.utils.extensions.inflate
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

fun ViewGroup.addView(@LayoutRes resId: Int): View {
    val view = inflate(resId)
    addView(view)
    return view
}
