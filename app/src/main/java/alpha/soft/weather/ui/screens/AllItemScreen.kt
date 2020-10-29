package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenAllItemBinding
import alpha.soft.weather.model.AllItem
import alpha.soft.weather.model.MapData
import alpha.soft.weather.model.Submenu
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.AllItemAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.AllItemViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.mainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

class AllItemScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenAllItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AllItemAdapter
    private val viewModel: AllItemViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenAllItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
    }

    private fun injectDependency(fragment: AllItemScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())

        adapter = AllItemAdapter(object : AllItemAdapter.ItemInterface {
            override fun itemClick(id: String, regionId: String) {
                findNavController().navigate(
                    R.id.action_mainScreen_to_itemScreen, bundleOf(
                        Constants.BUNDLE to id,
                        Constants.REGION_ID to regionId
                    )
                )
            }

            override fun itemLocationClick(regionId: String, mapData: MapData) {
                mainViewModel().setMapData(arrayListOf(mapData))
                mainViewModel().setMapDistrictData(requireContext(), regionId)
                findNavController().navigate(
                    R.id.action_mainScreen_to_mapScreen,
                    bundleOf(Constants.BUNDLE to false)
                )
            }
        })

        binding.apply {
            rvAllItem.adapter = adapter
            rvAllItem.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<List<AllItem>> {
        adapter.submitList(it)
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

    override fun refreshProgress() {
        viewModel.get(requireContext())
    }
}
