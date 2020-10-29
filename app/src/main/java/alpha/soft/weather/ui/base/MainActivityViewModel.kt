package alpha.soft.weather.ui.base

import alpha.soft.weather.R
import alpha.soft.weather.model.*
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.Event
import alpha.soft.weather.utils.PreferencesUtil
import alpha.soft.weather.utils.extensions.clearAndAddAll
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.lang.Exception
import java.nio.charset.Charset

class MainActivityViewModel : ViewModel() {

    private val mapData = ArrayList<MapData>()
    private val gson by lazy { Gson() }
    private var mapDistrictData: MapDistrictData? = null
    val listMapData = ArrayList<MapData>()

    private val _setCardData = MutableLiveData<AllItem>()
    val setCardData: LiveData<AllItem> = _setCardData

    private val _enableLocation = MutableLiveData<Event<Unit>>()
    val enableLocation: LiveData<Event<Unit>> = _enableLocation

    private val _openInfoPage = MutableLiveData<Event<Unit>>()
    val openInfoPage: LiveData<Event<Unit>> = _openInfoPage

    private val _locationLiveData = MutableLiveData<Event<Location>>()
    val locationLiveData: LiveData<Event<Location>> = _locationLiveData

    private val _openMapInfoPage = MutableLiveData<Event<Unit>>()
    val openMapInfoPage: LiveData<Event<Unit>> = _openMapInfoPage

    private val _hideMapMenu = MutableLiveData<Event<Unit>>()
    val hideMapMenu: LiveData<Event<Unit>> = _hideMapMenu

    private val _setLanguage = MutableLiveData<Event<String>>()
    val setLanguage: LiveData<Event<String>> = _setLanguage

    private val _openMapPage = MutableLiveData<Event<Unit>>()
    val openMapPage: LiveData<Event<Unit>> = _openMapPage

    private val _postLiveData = MutableLiveData<MenuResponse>()
    val postLiveData: LiveData<MenuResponse> = _postLiveData

    private val _networkError = MutableLiveData<String>()
    val networkError: LiveData<String> = _networkError

    private val _serverError = MutableLiveData<String>()
    val serverError: LiveData<String> = _serverError

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _showProgress = MutableLiveData<Unit>()
    val showProgress: LiveData<Unit> = _showProgress

    private val _hideProgress = MutableLiveData<Unit>()
    val hideProgress: LiveData<Unit> = _hideProgress

    fun setCardData(item: AllItem) {
        _setCardData.postValue(item)
    }

    fun openMap() {
        _openMapPage.postValue(Event(Unit))
    }

    fun init(preferencesUtil: PreferencesUtil, postApi: PostApi, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = postApi.getMenu(preferencesUtil.getUiLocale())
                if (response.isSuccessful) {
                    _postLiveData.postValue(response.body())
                } else {
                    _serverError.postValue(response.message())
                }
                _hideProgress.postValue(Unit)
            } catch (e: Exception) {
                _hideProgress.postValue(Unit)
                if (Constants.NEWWORK_EXCEPTION.contains(e::class.java)) {
                    _networkError.postValue(context.getString(R.string.network_error))
                } else {
                    _errorMessage.postValue(context.getString(R.string.error))
                }
            }
        }
    }

    fun openInfo() {
        _openInfoPage.postValue(Event(Unit))
    }

    fun setLanguage(lan: String) {
        _setLanguage.postValue(Event(lan))
    }

    fun getMapData() = mapData

    fun setList(mapData: List<MapData>) {
        listMapData.clearAndAddAll(mapData)
    }

    fun setMapData(data: List<MapData>) {
        mapData.clearAndAddAll(data)
    }

    fun openMapInfo() {
        _openMapInfoPage.postValue(Event(Unit))
    }

    fun hideMapMenu() {
        _hideMapMenu.postValue(Event(Unit))
    }

    fun setMapDistrictData(context: Context, id: String) {
        mapDistrictData = gson.fromJson<MapDistrictData>(
            loadJSONFromAsset("$id.json", context),
            MapDistrictData::class.java
        )
        Log.d("TTT", "$mapDistrictData")
    }

    fun setLocation(it: Location) {
        _locationLiveData.postValue(Event(it))
    }

    fun getMapDistrict() = mapDistrictData

    private fun loadJSONFromAsset(fileName: String, context: Context): String? {
        val json: String?
        json = try {
            val ins: InputStream = context.assets.open(fileName)
            val size: Int = ins.available()
            val buffer = ByteArray(size)
            ins.read(buffer)
            ins.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: Exception) {
            ex.printStackTrace()
            Log.d("TTT", "$ex")
            return null
        }
        return json
    }

    fun enableLocation() {
        _setCardData.postValue(_setCardData.value)
        _enableLocation.postValue(Event(Unit))
    }
}