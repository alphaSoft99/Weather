package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenNewsLetterBinding
import alpha.soft.weather.model.FilterResponse
import alpha.soft.weather.model.Month
import alpha.soft.weather.model.NewsLetterData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.MonthAdapter
import alpha.soft.weather.ui.adapters.NewsLetterAdapter
import alpha.soft.weather.ui.adapters.YearAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.NewsLetterViewModel
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.PopupWindow
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

class NewsLetterScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private val yearList = ArrayList<String>()
    private val monthList = ArrayList<Month>()
    private val adapterYear = YearAdapter(yearList)
    private val adapterMonth = MonthAdapter(monthList)
    private var _binding: ScreenNewsLetterBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NewsLetterAdapter
    private val viewModel: NewsLetterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenNewsLetterBinding.inflate(inflater, container, false)
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
            tvMonth.setOnClickListener {
                showMonth()
            }
        }
    }

    private fun injectDependency(fragment: NewsLetterScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {

        activity().showInfoMenu()

        viewModel.init(preferencesUtil, postApi)
        viewModel.getFilter(requireContext())
        viewModel.get(requireContext())

        adapter = NewsLetterAdapter(object : NewsLetterAdapter.ItemInterface {
            override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
            }
        })

        binding.apply {
            tvMonth.text = getString(viewModel.getMonth())
            tvYear.text = viewModel.getYear()
            monthList.clearAndAdd(Month(getString(viewModel.getMonth())))
            yearList.clearAndAdd(viewModel.getYear())
            rvCriteria.adapter = adapter
            rvCriteria.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.filterLiveData.observe(viewLifecycleOwner, filterLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
        mainViewModel().openInfoPage.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(R.id.action_newsLetterScreen_to_waterCriteriaScreen)
            }
        })
    }

    private val postLiveDataObserver = Observer<List<NewsLetterData>> {
        if(it!=null && it.isNotEmpty()){
            adapter.submitList(it)
            binding.tvEmpty.gone()
        }
        else
            binding.tvEmpty.visible()
    }

    private val filterLiveDataObserver = Observer<FilterResponse> {
        monthList.clearAndAddAll(it.month)
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

    private fun showMonth() {
        binding.apply {
            val popup = PopupWindow(requireContext())
            val list = ListView(context)
            list.adapter = adapterMonth
            adapterMonth.setItemClickListener { pos, name ->
                popup.dismiss()
                tvMonth.text = name
                viewModel.setMonth(pos+1, requireContext())
            }
            popup.setBackgroundDrawable(getDrawable(requireContext(), R.drawable.popup_background))
            popup.isFocusable = true
            popup.contentView = list
            popup.width = tvMonth.width
            popup.showAsDropDown(tvMonth)
        }
    }

    override fun refreshProgress() {
        if (monthList.size < 2 && yearList.size < 2)
            viewModel.getFilter(requireContext())
        viewModel.get(requireContext())
    }

    override fun onDestroyView() {
        activity().hideInfoMenu()
        super.onDestroyView()
    }
}
