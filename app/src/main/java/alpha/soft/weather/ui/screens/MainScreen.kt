package alpha.soft.weather.ui.screens

import alpha.soft.weather.R
import alpha.soft.weather.model.AllItem
import alpha.soft.weather.model.MapData
import alpha.soft.weather.ui.adapters.MainPagerAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.GpsUtils
import alpha.soft.weather.utils.Utils
import alpha.soft.weather.utils.extensions.*
import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.screen_main.*

class MainScreen : BaseFragment() {

    private var isGPS = false
    private var dataCard = AllItem("", "#00CF6A", "1", "5", "41.3123363", "69.2787079", "", "")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        loadViews()
        bt_loc.setOnClickListener {
            mainViewModel().setMapData(
                arrayListOf(
                    MapData(
                        dataCard.id,
                        dataCard.region_id,
                        dataCard.lat.toDouble(),
                        dataCard.lon.toDouble(),
                        dataCard.color
                    )
                )
            )
            mainViewModel().setMapDistrictData(requireContext(), dataCard.region_id)
            findNavController().navigate(R.id.action_mainScreen_to_mapScreen)
        }
        bt_card.setOnClickListener {
            if (isGPS) {
                findNavController().navigate(
                    R.id.action_mainScreen_to_itemScreen,
                    bundleOf(Constants.BUNDLE to dataCard.id, Constants.REGION_ID to dataCard.region_id)
                )
            } else {
                checkLocation()
            }
        }
        bt_ok.setOnClickListener {
            checkPermission()
        }
    }

    private fun loadViews() {
        activity().showMapMenu()
        val list = arrayListOf(getString(R.string.area), getString(R.string.all_items))
        val pagerAdapter = MainPagerAdapter(activity(), list.size)
        vp_intro.adapter = pagerAdapter
        TabLayoutMediator(tab_layout, vp_intro) { tab, position ->
            tab.text = list[position]
            vp_intro.setCurrentItem(tab.position, true)
        }.attach()
        mainViewModel().enableLocation.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                tv_header.visible()
                bt_loc.visible()
                bt_ok.gone()
                tv_header.text = dataCard.title
                tv_subtitle.text = dataCard.category_title
            }
        })
        mainViewModel().setCardData.observe(viewLifecycleOwner, Observer {
            if (isGPS && it != null) {
                dataCard = it
                tv_header.text = it.title
                tv_subtitle.text = it.category_title
            }
        })
    }

    private fun checkLocation() {
        if (Utils.hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.onGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    // turn on GPS
                    isGPS = isGPSEnable
                    if (isGPSEnable) {
                        mainViewModel().enableLocation()
                    }
                }
            })
        }
    }

    private fun checkPermission() {
        if (Utils.hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.onGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    // turn on GPS
                    isGPS = isGPSEnable
                    if (isGPSEnable) {
                        mainViewModel().enableLocation()
                    }
                }
            })
        } else {
            ActivityCompat.requestPermissions(
                activity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        run loop@{
            grantResults.forEach {
                if (it != 0) {
                    checkPermission()
                    return@loop
                }
            }
        }
        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
                if (isGPSEnable) {
                    mainViewModel().enableLocation()
                }
            }
        })
    }

    override fun onDestroyView() {
        activity().hideMapMenu()
        super.onDestroyView()
    }
}
