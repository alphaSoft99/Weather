package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenItemBinding
import alpha.soft.weather.model.Concentration
import alpha.soft.weather.model.ItemViewResponse
import alpha.soft.weather.model.MapData
import alpha.soft.weather.model.Submenu
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.ConcentrationAdapter
import alpha.soft.weather.ui.adapters.GraphicAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.ItemViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.gone
import alpha.soft.weather.utils.extensions.mainViewModel
import alpha.soft.weather.utils.extensions.visible
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import javax.inject.Inject

class ItemScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var graphicAdapter: GraphicAdapter
    private lateinit var concentrationAdapter: ConcentrationAdapter
    private val viewModel: ItemViewModel by viewModels()
    private var regId = "5"
    private var id = "1"
    private var mapData: MapData? = null
    private var showMapButton = true
    private var submenu: Submenu? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
        binding.apply {
            btLocation.setOnClickListener {
                if (mapData != null) {
                    mainViewModel().setMapData(arrayListOf(mapData!!))
                }
                mainViewModel().setMapDistrictData(requireContext(), regId)
                findNavController().navigate(
                    R.id.action_itemScreen_to_mapScreen,
                    bundleOf(Constants.BUNDLE to false)
                )
            }
            btMore.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://meteo.uz"))
                startActivity(browserIntent)
            }
        }
    }

    private fun injectDependency(fragment: ItemScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {

        viewModel.init(preferencesUtil, postApi)
        id = arguments?.getString(Constants.BUNDLE) ?: "5"
        regId = arguments?.getString(Constants.REGION_ID) ?: "5"
        showMapButton = arguments?.getBoolean(Constants.SHOW_BUTTON, true) ?: true

        if (!showMapButton) {
            binding.btLocation.gone()
        } else {
            binding.btLocation.visible()
        }

        viewModel.get(requireContext(), id)

        graphicAdapter = GraphicAdapter(object : GraphicAdapter.ItemInterface {
            override fun itemClick(pos: Int) {
            }
        })

        concentrationAdapter = ConcentrationAdapter(object : ConcentrationAdapter.ItemInterface {
            override fun itemClick(pos: Int) {
                submenu = mainViewModel().postLiveData.value?.get(0)?.submenu?.get(1)
                findNavController().navigate(
                    R.id.action_itemScreen_to_aboutScreen, bundleOf(
                        Constants.TITLE to submenu?.title,
                        Constants.BODY to submenu?.short_content + submenu?.content
                    )
                )
            }
        })

        binding.apply {
            rvGraphic.adapter = graphicAdapter
            rvGraphic.layoutManager = LinearLayoutManager(context)
            rvConcentrations.adapter = concentrationAdapter
            rvConcentrations.isNestedScrollingEnabled = false
            rvConcentrations.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    @Suppress("DEPRECATION")
    private val postLiveDataObserver = Observer<ItemViewResponse> {

        binding.apply {
            if (it.data.si == "-" || it.data.status_moderator_main != "active") {
                layout.gone()
                tvEmpty.text = it.data.title
                cardEmpty.visible()
            } else {
                layout.visible()
                cardEmpty.gone()
                tvHeader.text = it.data.title
                tvTitle.text = it.data.category_title
                tvCity.text = it.pogoda.city
                tvDate.text = Html.fromHtml(it.pogoda.title)
                Glide.with(ivWeather.context).load(it.pogoda.img)
                    .apply { placeholder(R.drawable.logo) }.into(ivWeather)
                tvCelsius.text = it.pogoda.temperature
                tvSubtitle.text = it.pogoda.content
                tvCi.text = it.data.si
                tvMiddle.text = Html.fromHtml(
                    when (preferencesUtil.getUiLocale()) {
                        "uz" -> "Ифлослантирувчилар ўртача суткалик концентрациялари (мг/м<sup>3</sup>)"
                        "ru" -> "Среднесуточные концентрации загрязнителей (мг/м<sup>3</sup>)"
                        "en" -> "Average daily concentrations of pollutants (mg/m<sup>3</sup>)"
                        else -> "Среднесуточные концентрации загрязнителей (мг/м<sup>3</sup>)"
                    }
                )
                mapData = MapData(
                    it.data.id,
                    regId,
                    it.data.lat.toDouble(),
                    it.data.lon.toDouble(),
                    "#00CF6A"
                )
                tvCi.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it.data.color_si))
                ivRecommended.setImageResource(
                    when (it.recommendations.icon) {
                        "icons-02" -> R.drawable.ic_icons_02
                        "icons-03" -> R.drawable.ic_icons_03
                        "icons-04" -> R.drawable.ic_icons_04
                        "icons-05" -> R.drawable.ic_icons_05
                        else -> R.drawable.ic_icons_02
                    }
                )
                ivRecommended.setColorFilter(
                    Color.parseColor(it.recommendations.color_si),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                tvRecommendedText.text = it.recommendations.content
                tvRefreshDate.text = it.data.date
                tvMiddleDate.text = it.concentrations_date
            }
            val list = ArrayList<Concentration>()
            it.concentrations.forEach {
                if (it.k_value != "") {
                    list.add(it)
                }
            }
            concentrationAdapter.submitList(list)
            graphicAdapter.submitList(it.graphic)
        }
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

    override fun onDestroy() {
//        mainViewModel().setMapDistrictData(requireContext(), Constants.UZB)
//        mainViewModel().setMapData(mainViewModel().listMapData)
        super.onDestroy()
    }

    override fun refreshProgress() {
        viewModel.get(requireContext(), id)
    }
}
