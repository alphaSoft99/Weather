package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.databinding.ScreenNewsBinding
import alpha.soft.weather.model.NewsItemResponse
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.PhotoViewPager
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.NewsViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import javax.inject.Inject

class NewsScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi
    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenNewsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsViewModel by viewModels()
    private var id = "26"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
    }

    private fun injectDependency(fragment: NewsScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        id = arguments?.getString(Constants.BUNDLE) ?: "26"
        viewModel.get(requireContext(), id)
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<NewsItemResponse>{
        val adapter = PhotoViewPager(
            requireContext(),
            it.gallery
        )

        binding.apply {
            tvTitle.text = it.data.title
            tvSubtitle.webViewClient = WebViewClient()
            tvSubtitle.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            tvSubtitle.loadDataWithBaseURL("fake://not/needed", it.data.content, "text/html", "utf-8", "")

            tvDate.text = it.data.date
            tvViews.text = it.data.views
            photoPager.adapter = adapter
            indicator.count = it.gallery.size
            photoPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {/*empty*/

                }

                override fun onPageSelected(position: Int) {
                    indicator.selection = position
                }

                override fun onPageScrollStateChanged(state: Int) {/*empty*/

                }
            })
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

    override fun refreshProgress() {
        viewModel.get(requireContext(), id)
    }
}
