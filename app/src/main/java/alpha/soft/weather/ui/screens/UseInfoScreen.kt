package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenUseInfoBinding
import alpha.soft.weather.model.NewsData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.UseInfoAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.UseInfoViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
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
import javax.inject.Inject

class UseInfoScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi
    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenUseInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : UseInfoAdapter
    private val viewModel: UseInfoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenUseInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
    }

    private fun injectDependency(fragment: UseInfoScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())

        adapter = UseInfoAdapter(object: UseInfoAdapter.ItemInterface{
            override fun itemClick(id: String) {
                findNavController().navigate(R.id.action_useInfoScreen_to_newsScreen,  bundleOf(Constants.BUNDLE to id))
            }
        })

        binding.apply {
            rvUseInfo.adapter = adapter
            rvUseInfo.layoutManager = LinearLayoutManager(context)
        }
        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    private val postLiveDataObserver = Observer<List<NewsData>>{
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
