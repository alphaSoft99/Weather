package alpha.soft.weather.ui.viewmodels

import alpha.soft.weather.R
import alpha.soft.weather.model.FilterResponse
import alpha.soft.weather.model.NewsLetterData
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

class NewsLetterViewModel : ViewModel() {

    private lateinit var preference: PreferencesUtil
    private lateinit var api: PostApi
    private var year = "2020"
    private var month = 8
    private var calendar = Calendar.getInstance()

    init {
        calendar.add(Calendar.YEAR, 1900)
//        calendar.add(Calendar.MONTH, -1)
        year = "${calendar.time.year}"
        month = calendar.time.month + 1
    }

    private val _postLiveData = MutableLiveData<List<NewsLetterData>>()
    val postLiveData: LiveData<List<NewsLetterData>> = _postLiveData

    private val _filterLiveData = MutableLiveData<FilterResponse>()
    val filterLiveData: LiveData<FilterResponse> = _filterLiveData

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
                val response = api.getFilter(preference.getUiLocale())
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
                val response = api.getNewsLetter(preference.getUiLocale(), "$month", year)
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
        get(context)
    }

    fun setMonth(month: Int, context: Context) {
        this.month = month
        get(context)
    }

    fun getYear() = year
    fun getMonth() = when(month){
        1 -> R.string.month_1
        2 -> R.string.month_2
        3 -> R.string.month_3
        4 -> R.string.month_4
        5 -> R.string.month_5
        6 -> R.string.month_6
        7 -> R.string.month_7
        8 -> R.string.month_8
        9 -> R.string.month_9
        10 -> R.string.month_10
        11 -> R.string.month_11
        12 -> R.string.month_12
        else -> R.string.month_1
    }
}