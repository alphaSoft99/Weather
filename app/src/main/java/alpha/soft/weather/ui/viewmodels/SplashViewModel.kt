package alpha.soft.weather.ui.viewmodels

import alpha.soft.weather.utils.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel : ViewModel() {
    private val _nextScreen = MutableLiveData<Event<Unit>>()
    val nextScreen: LiveData<Event<Unit>> = _nextScreen

    init{
        viewModelScope.launch(Dispatchers.Main) {
            delay(2000)
            withContext(coroutineContext) {
                _nextScreen.postValue(Event(Unit))
            }
        }
    }

}