package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenSoilBinding
import alpha.soft.weather.model.*
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.YearAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.SoilViewModel
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import javax.inject.Inject

class SoilScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private val yearList = ArrayList<String>()
    private val adapterYear = YearAdapter(yearList)
    private var _binding: ScreenSoilBinding? = null
    private val binding get() = _binding!!
//    private lateinit var adapter: SoilAdapter
    private val viewModel: SoilViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenSoilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
        binding.apply {
            tvYear.setOnClickListener {
                showYear()
            }
            btFilter.setOnClickListener {
                viewModel.get(requireContext())
            }
        }
    }

    private fun injectDependency(fragment: SoilScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {

        viewModel.init(preferencesUtil, postApi)
        viewModel.getFilter(requireContext())
        viewModel.get(requireContext())

        /*adapter = SoilAdapter(object : SoilAdapter.ItemInterface {
            override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
            }
        })*/

        binding.apply {
            tvYear.text = viewModel.getYear()
            yearList.clearAndAdd(viewModel.getYear())
//            rvCriteria.adapter = adapter
//            rvCriteria.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.filterLiveData.observe(viewLifecycleOwner, filterLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<List<SoilData>> {
//        adapter.submitList(it)
        if(it.isNotEmpty())
            binding.apply {
                tvEmpty.gone()
                tvHeader.text = it[0].title
                tvBody.webViewClient = WebViewClient()
                tvBody.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                tvBody.loadDataWithBaseURL("fake://not/needed", it[0].content, "text/html", "utf-8", "")
            }
        else {
            binding.apply {
                tvHeader.text = ""
                tvBody.webViewClient = WebViewClient()
                tvBody.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                tvBody.loadDataWithBaseURL("fake://not/needed", "", "text/html", "utf-8", "")
                tvEmpty.visible()
            }
        }
    }

    private val filterLiveDataObserver = Observer<SoilFilterResponse> {
//        monthList.clearAndAddAll(it.month)
        yearList.clearAndAddAll(it.year)
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

    private fun showYear() {
        binding.apply {
            val popup = PopupWindow(requireContext())
            val list = ListView(context)
            list.adapter = adapterYear
            adapterYear.setItemClickListener { _, name ->
                popup.dismiss()
                tvYear.text = name
                viewModel.setYear(name, requireContext())
            }
            popup.setBackgroundDrawable(getDrawable(requireContext(), R.drawable.popup_background))
            popup.isFocusable = true
            popup.contentView = list
            popup.width = tvYear.width
            popup.showAsDropDown(tvYear)
        }
    }

    override fun refreshProgress() {
        if (yearList.size < 2)
            viewModel.getFilter(requireContext())
        viewModel.get(requireContext())
    }
}
