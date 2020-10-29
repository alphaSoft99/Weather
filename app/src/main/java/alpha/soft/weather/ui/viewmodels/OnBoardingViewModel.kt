package alpha.soft.weather.ui.viewmodels

import alpha.soft.weather.utils.Event
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData

class OnBoardingViewModel : ViewModel() {

    private val _nextScreen = MutableLiveData<Event<Unit>>()
    val nextScreen: LiveData<Event<Unit>> = _nextScreen

    fun nextScreen(){
        _nextScreen.postValue(Event(Unit))
    }

}