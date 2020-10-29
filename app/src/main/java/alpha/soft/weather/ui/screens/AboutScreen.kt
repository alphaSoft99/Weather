package alpha.soft.weather.ui.screens

import androidx.lifecycle.Observer
import alpha.soft.weather.App
import alpha.soft.weather.databinding.ScreenAboutBinding
import alpha.soft.weather.model.PollutantsData
import alpha.soft.weather.model.RecommendationData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.AboutAdapter
import alpha.soft.weather.ui.adapters.PopulationAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.AboutViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.screen_about.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class AboutScreen : BaseFragment() {


    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenAboutBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AboutAdapter
    private lateinit var adapterPopulationMain: PopulationAdapter
    private lateinit var adapterPopulationSp: PopulationAdapter
    lateinit var intList: ArrayList<RecommendationData>

    private val viewModel: AboutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()

    }

    private fun injectDependency(fragment: AboutScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {


        val title = arguments?.getString(Constants.TITLE) ?: ""
        val newBody = arguments?.getString(Constants.NEW_BODY) ?: ""
        val body = arguments?.getString(Constants.BODY) ?: ""
        val IDS = arguments?.getString(Constants.IDS) ?: ""

        if (newBody != "") {
            viewModel.init(preferencesUtil, postApi)
            if (IDS == "3") {
                viewModel.get(requireContext())
                adapter = AboutAdapter(object : AboutAdapter.ItemInterface {
                    override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
                    }
                })
                viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
            } else if (IDS == "2") {
                viewModel.getPop(requireContext())

                adapterPopulationMain = PopulationAdapter(object : PopulationAdapter.ItemInterface {
                    override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
                    }
                })
                adapterPopulationSp = PopulationAdapter(object : PopulationAdapter.ItemInterface {
                    override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
                    }
                })

                viewModel.postLiveDataP.observe(viewLifecycleOwner, postLiveDataObserverPop)
            }

            viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
            viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
            viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
            viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
            viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)

        }

        binding.apply {
            tvTitle.text = title
            if (newBody != "") {
                tv_body_new.webViewClient = WebViewClient()
                tv_body_new.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                var dataStr = "<![CDATA[" +
                        "<html>" +
                        " <head></head>" +
                        " <body style=\"text-align:justify;color:black;background-color:white;\">" + newBody + "</body>" +
                        "</html>"
                tv_body_new.loadDataWithBaseURL(
                    "fake://not/needed",
                    dataStr,
                    "text/html",
                    "utf-8",
                    ""
                )
                if (IDS == "3") {
                    rv_about.adapter = adapter
                    rv_about.layoutManager = LinearLayoutManager(context)

                    rv_about.visibility = View.VISIBLE
                    recommendations_text.visibility = View.VISIBLE

                    tv_subtitle_new.visibility = View.GONE
                    population_text.visibility = View.GONE
                    tv_title_new.visibility = View.GONE
                    population_main.visibility = View.GONE
                    rv_main.visibility = View.GONE
                    population_sp.visibility = View.GONE
                    rv_sp.visibility = View.GONE
                } else if (IDS == "2") {

                    rv_main.adapter = adapterPopulationMain
                    rv_main.layoutManager = LinearLayoutManager(context)

                    rv_sp.adapter = adapterPopulationSp
                    rv_sp.layoutManager = LinearLayoutManager(context)


                    rv_about.visibility = View.GONE
                    recommendations_text.visibility = View.GONE

                    population_text.visibility = View.VISIBLE
                    tv_title_new.visibility = View.VISIBLE
                    tv_subtitle_new.visibility = View.VISIBLE
                    population_main.visibility = View.VISIBLE
                    rv_main.visibility = View.VISIBLE
                    population_sp.visibility = View.VISIBLE
                    rv_sp.visibility = View.VISIBLE
                }
            } else {
                rv_about.visibility = View.GONE
                recommendations_text.visibility = View.GONE

                population_text.visibility = View.GONE
                tv_title_new.visibility = View.GONE
                tv_subtitle_new.visibility = View.GONE
                population_main.visibility = View.GONE
                rv_main.visibility = View.GONE
                population_sp.visibility = View.GONE
                rv_sp.visibility = View.GONE
            }

            tvBody.webViewClient = WebViewClient()
            tvBody.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            var dataStr = "<![CDATA[" +
                    "<html>" +
                    " <head></head>" +
                    " <body style=\"text-align:justify;color:black;background-color:white;\">" + body + "</body>" +
                    "</html>"
            tvBody.loadDataWithBaseURL("fake://not/needed", dataStr, "text/html", "utf-8", "")
        }
    }


    private val postLiveDataObserver = Observer<List<RecommendationData>> {

        intList = ArrayList()

        for (x in it.size - 1 downTo 0) {
            intList.add(it[x])

        }

        adapter.submitList(intList)
    }

    private val postLiveDataObserverPop = Observer<PollutantsData> {
        adapterPopulationSp.submitList(it.specific)
        adapterPopulationMain.submitList(it.main)
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

    /*override fun onDestroyView() {
        activity().applyLocale()
        super.onDestroyView()
    }*/

    override fun refreshProgress() {
        viewModel.get(requireContext())
    }

}
