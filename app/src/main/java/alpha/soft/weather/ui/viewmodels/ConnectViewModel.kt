package alpha.soft.weather.ui.viewmodels

import alpha.soft.weather.R
import alpha.soft.weather.model.ContactData
import alpha.soft.weather.network.PostApi
import alpha.soft.weather.utils.Constants
import alpha.soft.weather.utils.PreferencesUtil
import android.content.Context
import android.text.TextWatcher
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ConnectViewModel : ViewModel() {

    private lateinit var preference: PreferencesUtil
    private lateinit var api: PostApi

    private val _postLiveData = MutableLiveData<String>()
    val postLiveData: LiveData<String> = _postLiveData

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

    fun setData(context: Context, name: String, email: String, info: String) {
        if(name.isEmpty()){
            return
        }
        if(email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return
        }
        if(info.isEmpty()){
            return
        }
        _showProgress.postValue(Unit)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.sendMessage(preference.getUiLocale(), name, email, info)
                if (response.isSuccessful) {
                    _postLiveData.postValue(response.body()?.message)
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
}