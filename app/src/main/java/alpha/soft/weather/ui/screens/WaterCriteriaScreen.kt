package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.databinding.ScreenWaterCriteriaBinding
import alpha.soft.weather.model.CriteriaData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.WaterCriteriaAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.WaterCriteriaViewModel
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

class WaterCriteriaScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi
    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenWaterCriteriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : WaterCriteriaAdapter
    private val viewModel: WaterCriteriaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenWaterCriteriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
    }

    private fun injectDependency(fragment: WaterCriteriaScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())

        adapter = WaterCriteriaAdapter(object: WaterCriteriaAdapter.ItemInterface{
            override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
            }
        })

        binding.apply {
            rvCriteria.adapter = adapter
            rvCriteria.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<List<CriteriaData>>{
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
