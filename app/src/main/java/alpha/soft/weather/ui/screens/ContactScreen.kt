package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenContactBinding
import alpha.soft.weather.model.ContactData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.PhoneAdapter
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.viewmodels.ContactViewModel
import alpha.soft.weather.utils.IntentUtil
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidessence.lib.RichTextView
import javax.inject.Inject

class ContactScreen : BaseFragment() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenContactBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ContactViewModel by viewModels()
    private lateinit var adapterPhone: PhoneAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        loadViews()

    }

    private fun injectDependency(fragment: ContactScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }

    private fun loadViews() {
        viewModel.init(preferencesUtil, postApi)
        viewModel.get(requireContext())

        adapterPhone = PhoneAdapter(object : PhoneAdapter.ItemInterface {
            override fun itemClick(phone: String) {
                IntentUtil.openDialer(context, phone.replace("-", "").replace(" ", ""))
            }
        })
        binding.apply {
            rvPhone.adapter = adapterPhone
            rvPhone.layoutManager = LinearLayoutManager(context)
        }

        viewModel.postLiveData.observe(viewLifecycleOwner, postLiveDataObserver)
        viewModel.networkError.observe(viewLifecycleOwner, networkErrorObserver)
        viewModel.serverError.observe(viewLifecycleOwner, serverErrorObserver)
        viewModel.errorMessage.observe(viewLifecycleOwner, errorMessageObserver)
        viewModel.hideProgress.observe(viewLifecycleOwner, hideProgressObserver)
        viewModel.showProgress.observe(viewLifecycleOwner, showProgressObserver)
    }

    @SuppressLint("SetTextI18n")
    private val postLiveDataObserver = Observer<ContactData> {
        binding.apply {
            tvAddress.text = "${getString(R.string.address)} ${it.address}"
            if (it.email.isNotEmpty())
            tvEmail.text = "${getString(R.string.email)} ${it.email[0]}"
            adapterPhone.submitList(it.phone)
            tvAddress.formatSpan(0, 7, RichTextView.FormatType.BOLD)
            tvEmail.formatSpan(0, 7, RichTextView.FormatType.BOLD)
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
        viewModel.get(requireContext())
    }
}
