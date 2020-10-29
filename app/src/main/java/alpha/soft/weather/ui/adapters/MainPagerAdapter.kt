package alpha.soft.weather.ui.adapters

import alpha.soft.weather.ui.screens.AllItemScreen
import alpha.soft.weather.ui.screens.AreasScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(
    fragmentActivity: FragmentActivity, private val count: Int
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = count

    override fun createFragment(position: Int): Fragment {
        if (position == 0)
        return AreasScreen()
        return AllItemScreen()
    }
}