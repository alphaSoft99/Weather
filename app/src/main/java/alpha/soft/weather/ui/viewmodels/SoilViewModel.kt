package alpha.soft.weather.ui.viewmodels

import alpha.soft.weather.R
import alpha.soft.weather.model.SoilFilterResponse
import alpha.soft.weather.model.SoilData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class SoilViewModel : ViewModel() {

    private lateinit var preference: PreferencesUtil
    private lateinit var api: PostApi
    private var year = "2020"
    private var calendar = Calendar.getInstance()

    init {
        calendar.add(Calendar.YEAR, 1900)
//        calendar.add(Calendar.YEAR, -1)
        year = "${calendar.time.year}"
    }

    private val _postLiveData = MutableLiveData<List<SoilData>>()
    val postLiveData: LiveData<List<SoilData>> = _postLiveData

    private val _filterLiveData = MutableLiveData<SoilFilterResponse>()
    val filterLiveData: LiveData<SoilFilterResponse> = _filterLiveData

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

    fun init(preferencesUtil: PreferencesUtil, postApi: PostApi) {
        this.api = postApi
        this.preference = preferencesUtil
    }

    fun getFilter(context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getSoilFilter(preference.getUiLocale())
                if (response.isSuccessful) {
                    _filterLiveData.postValue(response.body())
                } else {
                    _serverError.postValue(response.message())
                }
            } catch (e: Exception) {
                if (Constants.NEWWORK_EXCEPTION.contains(e::class.java)) {
                    _networkError.postValue(context.getString(R.string.network_error))
                } else {
                    _errorMessage.postValue(context.getString(R.string.error))
                }
            }
        }
    }

    fun get(context: Context) {
        _showProgress.postValue(Unit)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getSoil(preference.getUiLocale(), year)
                if (response.isSuccessful) {
                    _postLiveData.postValue(response.body()?.data ?: emptyList())
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

    fun setYear(year: String, context: Context) {
        this.year = year
    }

    fun getYear() = year
}