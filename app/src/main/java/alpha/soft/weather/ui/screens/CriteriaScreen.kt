package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenCriteriaBinding
import alpha.soft.weather.model.CriteriaData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.CriteriaAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.CriteriaViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import java.util.*
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.screen_criteria.*
import javax.inject.Inject


class CriteriaScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenCriteriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CriteriaAdapter
    private val viewModel: CriteriaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenCriteriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
    }

    private fun injectDependency(fragment: CriteriaScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())

        adapter = CriteriaAdapter(object : CriteriaAdapter.ItemInterface {
            override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
            }
        })
        var title = arguments?.getString(Constants.TITLE) ?: ""

        var body = arguments?.getString(Constants.BODY) ?: ""

        binding.apply {
            tvHeader.webViewClient = WebViewClient()
            tvHeader.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            title = "<![CDATA[" +
                    "<html>" +
                    " <head></head>" +
                    " <body style=\"text-align:justify;color:black;background-color:white;\">" + title + "</body>" +
                    "</html>"
            tvHeader.loadDataWithBaseURL("fake://not/needed", title, "text/html", "utf-8", "")

            rvCriteria.adapter = adapter
            rvCriteria.layoutManager = LinearLayoutManager(context)

            tv_next.setOnClickListener {
                val fragmentTransaction: FragmentTransaction =
                    childFragmentManager.beginTransaction()
                val profileFragment: Fragment = NewsLetterScreen()
                fragmentTransaction
                    .add(
                        R.id.frame_cr,
                        profileFragment
                    )
                fragmentTransaction.commit()
            }

            var body = "<![CDATA[" +
                    "<html>" +
                    " <head></head>" +
                    " <body style=\"text-align:justify;color:black;background-color:white;\">" + body + "</body>" +
                    "</html>"
            tv_body.webViewClient = WebViewClient()
            tv_body.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            tv_body.loadDataWithBaseURL("fake://not/needed", body, "text/html", "utf-8", "")

        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<List<CriteriaData>> {
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
