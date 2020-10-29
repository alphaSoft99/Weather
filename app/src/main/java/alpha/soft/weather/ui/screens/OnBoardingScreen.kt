package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.databinding.ScreenOnBoardingBinding
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.base.MainActivity
import alpha.soft.weather.ui.viewmodels.OnBoardingViewModel
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.GpsUtils
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.Utils
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.activitySplash
import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import javax.inject.Inject

class OnBoardingScreen : BaseFragment() {

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private val viewModel: OnBoardingViewModel by viewModels()
    private var isGPS = false
    private var _binding: ScreenOnBoardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activitySplash().applyLocale()
        injectDependency()
        loadViews()
        binding.btOk.setOnClickListener {
            checkPermission()
        }
        binding.btNoLocation.setOnClickListener {
            viewModel.nextScreen()
        }
    }

    private fun injectDependency() {
        (activitySplash().application as App).getApiComponent().inject(this)
    }

    private fun loadViews() {
        viewModel.nextScreen.observe(viewLifecycleOwner, Observer{
            it.getContentIfNotHandled()?.let {
                preferencesUtil.setFirstOpen(false)
                activitySplash().finish()
                startActivity(Intent(context, MainActivity::class.java))
            }
        })
    }

    private fun checkPermission() {
        if (Utils.hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.onGpsListener {
                override fun gpsStatus(isGPSEnable: Boolean) {
                    // turn on GPS
                    isGPS = isGPSEnable
                    if(isGPSEnable){
                        viewModel.nextScreen()
                    }
                }
            })
        } else {
            ActivityCompat.requestPermissions(
                activitySplash(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Constants.REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        run loop@{
            grantResults.forEach {
                if (it != 0){
                    checkPermission()
                    return@loop
                }
            }
        }
        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                // turn on GPS
                isGPS = isGPSEnable
                if(isGPSEnable){
                    viewModel.nextScreen()
                }
            }
        })
    }

    /*override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true // flag maintain before get location
            }
        }
    }*/
}
