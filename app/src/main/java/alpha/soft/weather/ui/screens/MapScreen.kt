@file:Suppress("DEPRECATION")

package alpha.soft.weather.ui.screens

import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenMapBinding
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.mainViewModel
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.NonNull
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style

@Suppress("DEPRECATION", "UNCHECKED_CAST", "NestedLambdaShadowedImplicitParameter")
class MapScreen : BaseFragment() {

    private var _binding: ScreenMapBinding? = null
    private val binding get() = _binding!!
    private val polylineOptions = ArrayList<PolylineOptions>()
    private val markerOptionsChild = MarkerOptions()
    private var isIconClick = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        _binding = ScreenMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        activity().showMapInfoMenu()

        isIconClick = arguments?.getBoolean(Constants.BUNDLE, true)?:true

        mainViewModel().openMapInfoPage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_mapScreen_to_mapInfoScreen)
            }
        })

        mainViewModel().getMapDistrict()?.forEach {
            for (i in it.geojson.coordinates.indices) {
                polylineOptions.clear()
                if (it.geojson.coordinates[i] is List<*>) {
                    it.geojson.coordinates.forEach {
                        val polylineOption = PolylineOptions()
                        if (it is List<*> && it[0] is List<*>) {
                            it.forEach {
                                if (it is List<*> && it[0] is List<*>) {
                                    it.forEach {
                                        if (it is List<*> && it[0] is List<*>) {
                                            it.forEach {
                                                if (it is List<*> && it[0] is List<*>) {
                                                    it.forEach { p ->
                                                        val point = p as List<Double>
                                                        polylineOption.polyline.addPoint(
                                                            LatLng(
                                                                point[1],
                                                                point[0]
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    val point = it as List<Double>
                                                    polylineOption.polyline.addPoint(
                                                        LatLng(
                                                            point[1],
                                                            point[0]
                                                        )
                                                    )
                                                }
                                            }
                                        } else {
                                            val point = it as List<Double>
                                            polylineOption.polyline.addPoint(
                                                LatLng(
                                                    point[1],
                                                    point[0]
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    val point = it as List<Double>
                                    polylineOption.polyline.addPoint(LatLng(point[1], point[0]))
                                }
                            }
                            polylineOptions.add(polylineOption)
                        }
                    }
                }
            }
        }

        polylineOptions.forEach {
            it.polyline.color = Color.parseColor("#70377B")
            it.polyline.width = 3f
        }

        val position = CameraPosition.Builder()
        if (mainViewModel().getMapDistrict()?.get(0)?.zoom != 4.0) {
            if (mainViewModel().getMapData().isNotEmpty()) {
                position.target(LatLng(mainViewModel().getMapData()[0].lat, mainViewModel().getMapData()[0].lng))
            } else {
                position.target(
                    LatLng(
                        mainViewModel().getMapDistrict()?.get(0)?.center_map?.lat?.toDouble() ?: 41.3123363,
                        mainViewModel().getMapDistrict()?.get(0)?.center_map?.lon?.toDouble() ?: 69.2787079
                    )
                )
            }
        } else {
            position.target(LatLng(42.0, 64.0))
        }
        position.zoom(mainViewModel().getMapDistrict()?.get(0)?.zoom ?: 10.0)

        markerOptionsChild.icon(
            drawableToIcon(
                requireContext(),
                R.drawable.ic_map_icon_s,
                Color.parseColor("#FFFFFF")
            )
        )
        binding.mapView.getMapAsync { mapboxMap ->
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position.build()), 700)
            mapboxMap.addPolylines(polylineOptions)

            if (isIconClick){
                mapboxMap.setOnMarkerClickListener { marker ->
                    mainViewModel().getMapData().forEach {
                        if (it.id == marker.title)
                            findNavController().navigate(R.id.action_mapScreen_to_itemScreen, bundleOf(Constants.BUNDLE to it.id, Constants.REGION_ID to it.regionId, Constants.SHOW_BUTTON to false))}
                    false
                }
            }
            mainViewModel().getMapData().forEach {
                val point = LatLng(it.lat, it.lng)
                val markerOptions = MarkerOptions()
                markerOptions.icon(
                    drawableToIcon(
                        requireContext(),
                        R.drawable.ic_icon_mix,
                        Color.parseColor(it.markerId)
                    )
                )
                markerOptions.position(point)
                if(isIconClick){
                    markerOptions.title = it.id
                    markerOptionsChild.title = it.id
                }
                mapboxMap.addMarker(markerOptions)
                markerOptionsChild.position(point)
                mapboxMap.addMarker(markerOptionsChild)
            }
            mapboxMap.setStyle(
                Style.Builder()
                    .fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41") // Add the SymbolLayer icon image to the map style
            )
        }
    }

    private fun drawableToIcon(
        @NonNull context: Context,
        @DrawableRes id: Int,
        @ColorInt colorRes: Int
    ): Icon? {
        val vectorDrawable: Drawable =
            ResourcesCompat.getDrawable(context.resources, id, context.theme)!!
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, colorRes)
        vectorDrawable.draw(canvas)
        return IconFactory.getInstance(context).fromBitmap(bitmap)
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroyView() {
        binding.mapView.onDestroy()
        activity().hideMapInfoMenu()
        super.onDestroyView()
    }
}
