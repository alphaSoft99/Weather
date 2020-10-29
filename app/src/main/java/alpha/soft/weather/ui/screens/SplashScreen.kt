package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.databinding.ScreenSplashBinding
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.ui.base.MainActivity
import alpha.soft.weather.ui.viewmodels.SplashViewModel
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activitySplash
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import javax.inject.Inject

class SplashScreen : BaseFragment() {

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private val viewModel: SplashViewModel by viewModels()
    private var _binding: ScreenSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activitySplash().applyLocale()
        injectDependency()
        loadViews()
    }

    private fun injectDependency() {
        (activitySplash().application as App).getApiComponent().inject(this)
    }

    private fun loadViews() {
        viewModel.nextScreen.observe(viewLifecycleOwner, Observer{
            it.getContentIfNotHandled()?.let {
                if(preferencesUtil.firstOpen()){
                    findNavController().navigate(R.id.action_splashScreen_to_onBoardingScreen)
                }
                else {
                    activitySplash().finish()
                    startActivity(Intent(context, MainActivity::class.java))
                }
            }
        })
    }
}
