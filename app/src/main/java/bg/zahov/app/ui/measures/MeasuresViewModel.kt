package bg.zahov.app.ui.measures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.getUserProvider
import kotlinx.coroutines.launch

class MeasuresViewModel(application: Application) : AndroidViewModel(application) {
    private val userProvider by lazy {
        application.getUserProvider()
    }

    fun onMeasurementClick(title: String) {
        viewModelScope.launch {
            MeasurementType.fromKey(title)?.let {
                userProvider.selectMeasure(it)
            }
        }
    }
}