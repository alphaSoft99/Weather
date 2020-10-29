package alpha.soft.weather.ui.base

import alpha.soft.weather.App
import alpha.soft.weather.R
import alpha.soft.weather.model.MenuResponse
import alpha.soft.weather.model.Submenu
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.ui.adapters.MenuAdapter
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_drawer.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var postApi: PostApi

    @Inject
    lateinit var preferencesUtil: PreferencesUtil

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var adapter: MenuAdapter
    private lateinit var viewModel: MainActivityViewModel
    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(
                R.id.mainScreen
            ), drawerLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyLocale()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        injectDependency()
        adjustFontScale(resources.configuration)
        if (preferencesUtil.firstOpen()) {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
        setupNavigation()
        loadData()
        navigationItemClick()
    }

    fun applyLocale() {
        val app = application as App
        app.applyLocale(this)
    }

    private fun injectDependency() {
        (application as App).getApiComponent().inject(this)
    }

    @SuppressLint("RtlHardcoded")
    private fun loadData() {
        /*FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("Weather_App")
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { p0 ->
            Log.d("TTT", "${p0?.token}")
        }*/

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        adapter = MenuAdapter(object : MenuAdapter.ItemInterface {
            override fun itemClick(pos: Int, categoryId: String, submenu: Submenu) {
                when {
                    categoryId == "1" && pos == 3 -> {
                        navController.navigate(
                            R.id.action_mainScreen_to_aboutScreen,
                            bundleOf(
                                Constants.TITLE to submenu.title,
                                Constants.IDS to "3",
                                Constants.NEW_BODY to submenu.content,
                                Constants.BODY to submenu.short_content
                            )
                        )
                    }
                    categoryId == "1" && pos == 2 -> {
                        navController.navigate(
                            R.id.action_mainScreen_to_aboutScreen,
                            bundleOf(
                                Constants.TITLE to submenu.title,
                                Constants.NEW_BODY to submenu.content,
                                Constants.IDS to "2",
                                Constants.BODY to submenu.short_content
                            )
                        )
                    }
                    categoryId == "1" && pos == 4 -> {
                        navController.navigate(R.id.action_mainScreen_to_useInfoScreen)
                    }
                    categoryId == "1" && pos == 5 -> {
                        viewModel.setMapData(viewModel.listMapData)
                        viewModel.setMapDistrictData(this@MainActivity, Constants.UZB)
                        viewModel.openMap()
                    }
                    categoryId == "2" && pos == 0 -> {
                        navController.navigate(
                            R.id.action_mainScreen_to_criteriaScreen, bundleOf(
                                Constants.TITLE to submenu.short_content,
                                Constants.BODY to submenu.content
                            )
                        )
                    }
                    categoryId == "2" && pos == 1 -> {
                        navController.navigate(R.id.action_mainScreen_to_newsLetterScreen)
                    }
                    categoryId == "3" && pos == 1 -> {
                        navController.navigate(R.id.action_mainScreen_to_soilScreen)
                    }
                    else -> {
                        navController.navigate(
                            R.id.action_mainScreen_to_aboutScreen,
                            bundleOf(
                                Constants.TITLE to submenu.title,
                                Constants.BODY to submenu.short_content + submenu.content
                            )
                        )
                    }
                }
                drawerLayout.closeDrawer(Gravity.LEFT)
            }
        })

        rv_menu.adapter = adapter
        rv_menu.layoutManager = LinearLayoutManager(this)

        @Suppress("DEPRECATION")
        viewModel = ViewModelProviders.of(this)[MainActivityViewModel::class.java]
        viewModel.init(preferencesUtil, postApi, this)

        viewModel.setLanguage.observe(this, Observer { it ->
            it.getContentIfNotHandled()?.let {
                preferencesUtil.setUiLocale(it)
                recreate()
            }
        })
        viewModel.openMapPage.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                navController.navigate(R.id.action_mainScreen_to_mapScreen)
                drawerLayout.closeDrawer(Gravity.LEFT)
            }
        })
        viewModel.postLiveData.observe(this, postLiveDataObserver)
        viewModel.networkError.observe(this, networkErrorObserver)
        viewModel.serverError.observe(this, serverErrorObserver)
        viewModel.errorMessage.observe(this, errorMessageObserver)
        viewModel.hideProgress.observe(this, hideProgressObserver)
        viewModel.showProgress.observe(this, showProgressObserver)


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                viewModel.setLocation(it)
            }
        }
    }

    private val postLiveDataObserver = Observer<MenuResponse> {
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
    }

    private val showProgressObserver = Observer<Unit> {
    }

    private fun setupNavigation() {
        /*supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false;
        toolbar.setNavigationIcon(R.drawable.ic_hamburger);
        toggle.syncState()*/
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map, menu)
        viewModel.hideMapMenu()
        return super.onCreateOptionsMenu(menu)
    }

    fun showMapMenu() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        toolbar.apply {
            inflateMenu(R.menu.map)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_map -> {
                        viewModel.setMapData(viewModel.listMapData)
                        viewModel.setMapDistrictData(this@MainActivity, Constants.UZB)
                        viewModel.openMap()
                    }
                    R.id.action_map_text -> {
                        viewModel.setMapData(viewModel.listMapData)
                        viewModel.setMapDistrictData(this@MainActivity, Constants.UZB)
                        viewModel.openMap()
                    }
                }
                true
            }
        }
    }

    fun hideMapMenu() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.apply {
            menu.removeItem(R.id.action_map)
            menu.removeItem(R.id.action_map_text)
        }
    }

    fun showInfoMenu() {
        toolbar.apply {
            inflateMenu(R.menu.info)
            setOnMenuItemClickListener {
                if(it.itemId == R.id.action_info)
                    viewModel.openInfo()
                return@setOnMenuItemClickListener true
            }
        }
    }

    fun showMapInfoMenu() {
        toolbar.apply {
            inflateMenu(R.menu.map_info)
            setOnMenuItemClickListener {
                if(it.itemId == R.id.action_map_info)
                    viewModel.openMapInfo()
                return@setOnMenuItemClickListener true
            }
        }
    }

    fun hideInfoMenu() {
        toolbar.apply {
            menu.removeItem(R.id.action_info)
        }
    }

    fun hideMapInfoMenu() {
        toolbar.apply {
            menu.removeItem(R.id.action_map_info)
        }
    }

    private fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale > 1) {
            configuration.fontScale = 1.toFloat()
            val metrics = resources.displayMetrics
            val wm =
                getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }

    @SuppressLint("RtlHardcoded")
    private fun navigationItemClick() {
        layout_contact.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_contactScreen)
            drawerLayout.closeDrawer(Gravity.LEFT)
        }
        layout_share.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "http://play.google.com/store/apps/details?id=$packageName"
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            drawerLayout.closeDrawer(Gravity.LEFT)
        }
        layout_language.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_languageScreen)
            drawerLayout.closeDrawer(Gravity.LEFT)
        }
        layout_connect.setOnClickListener {
            navController.navigate(R.id.action_mainScreen_to_connectScreen)
            drawerLayout.closeDrawer(Gravity.LEFT)
        }
    }

    override fun refreshProgress() {
        viewModel.init(preferencesUtil, postApi, this)
    }

    @SuppressLint("RtlHardcoded")
    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(Gravity.LEFT) -> {
                drawerLayout.closeDrawer(Gravity.LEFT)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

}