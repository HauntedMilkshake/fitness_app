package bg.zahov.app.ui.measures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.zahov.app.data.interfaces.MeasurementProvider
import bg.zahov.app.data.model.MeasurementType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing and providing UI state related to body measurements.
 * This class interacts with a [MeasurementProvider] to handle user interactions
 * with different measurement types.
 *
 * @property measurementProvider Provides measurement-related functionality.
 */

@HiltViewModel
class MeasuresViewModel @Inject constructor(
    private val measurementProvider: MeasurementProvider
) : ViewModel() {


    //A private mutable state flow that holds a list of all measurement names as strings.
    private val _uiState = MutableStateFlow(getAllMeasures())

    /**
     * A public state flow that exposes an immutable list of measurement names as strings to observers.
     * Used for displaying or selecting measurement types within the UI.
     */
    val uiState: StateFlow<List<String>> = _uiState

    /**
     * Handles the click action on a measurement item.
     * Updates the UI state with the selected measurement's title and triggers
     * the measurement selection in the [MeasurementProvider].
     *
     * @param title The title of the measurement that was clicked.
     */
    fun onMeasurementClick(title: String) {
        viewModelScope.launch {
            MeasurementType.entries.firstOrNull { it.key.equals(title, true) }?.let {
                measurementProvider.selectMeasure(it)
            }
        }
    }

    /**
     * Retrieves a list of all available measurement titles.
     *
     * @return A list of strings representing each measurement's title.
     */
    private fun getAllMeasures() =
        enumValues<MeasurementType>().map { it.key }
}
