package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenMapInfoBinding
import alpha.soft.weather.model.ManualData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.MapInfoAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.MapInfoViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import javax.inject.Inject

class MapInfoScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private lateinit var adapter : MapInfoAdapter
    private var _binding: ScreenMapInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapInfoViewModel by viewModels()
    private var title = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenMapInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
        binding.btQuestion.setOnClickListener {
            showInfoDialog()
        }
    }

    private fun injectDependency(fragment: MapInfoScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())


        adapter = MapInfoAdapter(object: MapInfoAdapter.ItemInterface{
            override fun itemClick(pos: Int) {
//                findNavController().navigate(R.id.action_my_orders_to_orderDetail,  bundleOf(Constants.BUNDLE to Constants.PENDING, Constants.ORDER_ID to id))
            }
        })
        binding.apply {
            rvMapInfo.adapter = adapter
            rvMapInfo.layoutManager = LinearLayoutManager(context)
        }

        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    @SuppressLint("SetTextI18n")
    private val postLiveDataObserver = Observer<ManualData> {
        binding.tvTitle.text = it.content
        binding.tvSubtitle.text = it.title
        title = it.iza_content
        adapter.submitList(it.iza)
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

    private fun showInfoDialog(){
        AlertDialog.Builder(requireContext())
            .setMessage(title)
            .setPositiveButton(R.string.ok) { _, _ ->
            }
            .show()
    }
    override fun refreshProgress() {
        viewModel.get(requireContext())
    }
}
