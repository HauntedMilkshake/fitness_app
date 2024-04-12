package bg.zahov.app.ui.measures

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.model.MeasurementType
import bg.zahov.app.getMeasurementsProvider
import kotlinx.coroutines.launch

class MeasuresViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementProvider by lazy {
        application.getMeasurementsProvider()
    }

    fun onMeasurementClick(title: String) {
        viewModelScope.launch {
            MeasurementType.fromKey(title)?.let {
                measurementProvider.selectMeasure(it)
            }
        }
    }
}