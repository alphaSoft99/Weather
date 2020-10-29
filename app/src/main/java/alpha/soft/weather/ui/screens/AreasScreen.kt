package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenAreaBinding
import alpha.soft.weather.model.AllItem
import alpha.soft.weather.model.AreaData
import alpha.soft.weather.model.MapData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.AreaAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.AreaViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.clearAndAddAll
import alpha.soft.weather.utils.extensions.mainViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.mapboxsdk.geometry.LatLng
import javax.inject.Inject
import kotlin.math.abs

class AreasScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi
    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenAreaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : AreaAdapter
    private val viewModel: AreaViewModel by viewModels()
    private var data = ArrayList<AllItem>()
    private var myLocation = LatLng(41.3123363, 69.2787079)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenAreaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
    }

    private fun injectDependency(fragment: AreasScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())

        adapter = AreaAdapter(object: AreaAdapter.ItemInterface{
            override fun itemClick(regionId: String) {
                val data = ArrayList<MapData>()
                mainViewModel().listMapData.forEach {
                    if(regionId == it.regionId){
                        data.add(it)
                    }
                }
                mainViewModel().setMapData(data)
                mainViewModel().setMapDistrictData(requireContext(), regionId)
                mainViewModel().openMap()
            }

            override fun itemChildClick(regionId: String, id: String) {
                findNavController().navigate(
                        R.id.action_mainScreen_to_itemScreen, bundleOf(
                            Constants.BUNDLE to id, Constants.REGION_ID to regionId)
                    )
            }

            override fun itemChildLocationClick(regionId: String, mapData: MapData) {
                mainViewModel().setMapData(arrayListOf(mapData))
                        mainViewModel().setMapDistrictData(requireContext(), regionId)
                        findNavController().navigate(
                            R.id.action_mainScreen_to_mapScreen, bundleOf(
                                Constants.BUNDLE to false))
            }
        })

        mainViewModel().locationLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                myLocation = LatLng(it.latitude, it.longitude)
                if (data.isNotEmpty())
                    setCardData()
            }
        })

        binding.apply {
            rvAllItem.adapter = adapter
            rvAllItem.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.postChildLiveData.observe(viewLifecycleOwner, postChildLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<List<AreaData>>{
        adapter.submitList(it)
    }

    private val postChildLiveDataObserver = Observer<List<AllItem>>{
        data.clearAndAddAll(it)
        if (it.isNotEmpty()) {
            setCardData()
        }
        val data = ArrayList<MapData>()
        it.forEach { data.add(MapData(it.id, it.region_id, it.lat.toDouble(), it.lon.toDouble(), it.color)) }
        mainViewModel().setList(data)
        adapter.setAllItemData(it)
        adapter.notifyDataSetChanged()
    }

    private val networkErrorObserver = Observer<String> {
        showNetworkErrorDialog(it)
    }

    private val serverErrorObserver = Observer<String> {
        showExceptionDialog(it)
    }

    private val errorMessageObserver = Observer<String> {
        showExceptionDialog(it)
    }

    private val hideProgressObserver = Observer<Unit> {
        activity().hideProgress()
    }

    private val showProgressObserver = Observer<Unit> {
        activity().showProgress()
    }

    private fun setCardData() {
        var pos = 0;
        var min = abs((myLocation.latitude + myLocation.longitude) - (data[0].lat.toDouble() + data[0].lon.toDouble()))
        for (it in data.indices) {
            if (min > abs((myLocation.latitude + myLocation.longitude) - (data[it].lat.toDouble() + data[it].lon.toDouble()))) {
                min = abs((myLocation.latitude + myLocation.longitude) - (data[it].lat.toDouble() + data[it].lon.toDouble()))
                pos = it
            }
        }
        Log.d("TTT", "$pos")
        mainViewModel().setCardData(data[pos])
    }

    override fun refreshProgress() {
        viewModel.get(requireContext())
    }
}
