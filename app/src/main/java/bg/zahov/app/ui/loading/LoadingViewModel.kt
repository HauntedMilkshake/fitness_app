package bg.zahov.app.ui.loading

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getServiceErrorProvider
import bg.zahov.app.getUserProvider
import bg.zahov.fitness.app.R
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    private val userProvider by lazy {
        application.getUserProvider()
    }
    private val serviceError by lazy {
        application.getServiceErrorProvider()
    }
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    fun onAppStart() {
        viewModelScope.launch {
            try {
                if(userProvider.isAuthenticated()) {
                    userProvider.initDataSources()
                    _state.postValue(State.Navigate(R.id.loading_to_home))
                } else {
                    _state.postValue(State.Navigate(R.id.loading_to_welcome))
                }
            } catch (e: Exception) {
               serviceError.stopApplication()
            }
        }
    }

    sealed interface State {
        data class Loading(val isDataLoading: Boolean) : State
        data class Navigate(val destination: Int?) : State
    }
}