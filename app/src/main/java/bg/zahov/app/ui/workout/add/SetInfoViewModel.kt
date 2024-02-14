package bg.zahov.app.ui.workout.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bg.zahov.app.data.model.SetType

class SetInfoViewModel(application: Application): AndroidViewModel(application) {

    private val _infoType = MutableLiveData(SetType.DEFAULT)
    val infoType: LiveData<SetType>
        get() = _infoType

    fun setState(state: SetType) {
        _infoType.value = state
    }

}