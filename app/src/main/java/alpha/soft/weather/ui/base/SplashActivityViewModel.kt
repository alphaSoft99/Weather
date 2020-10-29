package alpha.soft.weather.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashActivityViewModel : ViewModel() {

    private val _finishActivity = MutableLiveData<Unit>()
    val finishActivity : LiveData<Unit> = _finishActivity

    fun finish() {
        _finishActivity.postValue(Unit)
    }
}