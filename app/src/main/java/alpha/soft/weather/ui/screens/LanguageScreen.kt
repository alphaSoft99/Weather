package alpha.soft.weather.ui.screens

import alpha.soft.weather.App
import alpha.soft.weather.databinding.ScreenLanguageBinding
import alpha.soft.weather.ui.base.BaseFragment
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.activity
import alpha.soft.weather.utils.extensions.mainViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import javax.inject.Inject

class LanguageScreen : BaseFragment() {

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private var _binding: ScreenLanguageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ScreenLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity().applyLocale()
        injectDependency(this)
        mainViewModel().hideMapMenu.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                activity().hideMapMenu()
            }
        })

        binding.apply {
            when (preferencesUtil.getUiLocale()) {
                "ru" -> radioRus.isChecked = true
                "uz" -> radioUzb.isChecked = true
                "en" -> radioEng.isChecked = true
            }

            btRus.setOnClickListener {
                radioRus.isChecked = true
                radioEng.isChecked = false
                radioUzb.isChecked = false
                mainViewModel().setLanguage("ru")
            }
            btUzb.setOnClickListener {
                radioUzb.isChecked = true
                radioRus.isChecked = false
                radioEng.isChecked = false
                mainViewModel().setLanguage("uz")
            }
            btEng.setOnClickListener {
                radioEng.isChecked = true
                radioRus.isChecked = false
                radioUzb.isChecked = false
                mainViewModel().setLanguage("en")
            }

        }
    }

    private fun injectDependency(fragment: LanguageScreen) {
        (activity().application as App).getApiComponent().inject(fragment)
    }
}
