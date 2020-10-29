package alpha.soft.weather.ui.base

import alpha.soft.weather.App
import alpha.soft.weather.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class SplashActivity : AppCompatActivity() {

    private lateinit var viewModel: SplashActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        applyLocale()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        viewModel = ViewModelProviders.of(this)[SplashActivityViewModel::class.java]
        viewModel.finishActivity.observe(this, finishObserver)
    }

    fun applyLocale() {
        val app = application as App
        app.applyLocale(this)
    }

    private val finishObserver = Observer<Unit> {
        finish()
    }
}
