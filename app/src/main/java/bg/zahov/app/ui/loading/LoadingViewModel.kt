package bg.zahov.app.ui.loading

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.getUserProvider
import bg.zahov.fitness.app.R
import kotlinx.coroutines.launch

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    private val userProvider by lazy {
        application.getUserProvider()
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
                Log.e("auth error", e.message ?: "no message")
                _state.postValue(State.Error(e.message))
            }
        }
    }

    sealed interface State {
        data class Loading(val isDataLoading: Boolean) : State
        data class Error(val message: String?) : State
        data class Navigate(val destination: Int?) : State
    }
}