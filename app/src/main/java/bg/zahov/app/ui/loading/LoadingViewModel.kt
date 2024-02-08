package bg.zahov.app.ui.loading

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bg.zahov.app.MyApplication
import bg.zahov.app.data.exception.CriticalDataNullException
import bg.zahov.app.getUserProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoadingViewModel(application: Application) : AndroidViewModel(application) {
    private val userProvider by lazy {
        application.getUserProvider()
    }

    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        viewModelScope.launch {
            try {
                userProvider.getUser().collect {
                    if (it.name.isNotEmpty()) _state.postValue(State.Loading(false))
                }
            } catch (e: CriticalDataNullException) {
                _state.postValue(State.Error(e.message))
            }
        }
    }

    sealed interface State {
        data class Loading(val isDataLoading: Boolean) : State
        data class Error(val message: String?) : State
    }
}