package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenConnectBinding
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.ConnectViewModel
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.to
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.screen_connect.*
import javax.inject.Inject

class ConnectScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenConnectBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConnectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()
        binding.apply {
            btSend.setOnClickListener {
                viewModel.setData(requireContext(), etName.to(), etEmail.to(), etInfo.to())
            }
        }
    }

    private fun injectDependency(fragment: ConnectScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)

        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    @SuppressLint("SetTextI18n")
    private val postLiveDataObserver = Observer<String> {
        AlertDialog.Builder(requireContext())
            .setMessage(it)
            .setPositiveButton(R.string.ok) { _, _ ->
                findNavController().popBackStack()
            }
            .show()
    }

    private val networkErrorObserver = Observer<String> {

        showNetworkErrorDialog(it)
    }

    private val serverErrorObserver = Observer<String> {
        showExceptionDialog(it)
    }

    private val errorMessageObserver = Observer<String> {
        et_name.text = null
        et_email.text = null
        et_info.text = null
        showExceptionDialog(it)
    }

    private val hideProgressObserver = Observer<Unit> {
        activity().hideProgress()
    }

    private val showProgressObserver = Observer<Unit> {
        activity().showProgress()
    }

    override fun refreshProgress() {
        binding.apply {
            viewModel.setData(requireContext(), etName.to(), etEmail.to(), etInfo.to())
        }
    }
}
