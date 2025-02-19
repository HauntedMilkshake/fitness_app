package bg.zahov.app.ui.history.info.topbar

import androidx.lifecycle.ViewModel
import bg.zahov.app.data.interfaces.WorkoutProvider

/**
 * ViewModel for handling actions related to the top bar in the History Info screen.
 *
 * @property historyInfoTopBarHandler defines the actions to be performed when interacting with the top bar
 */
class HistoryInfoTopBarViewModel(private val historyInfoTopBarHandler: WorkoutProvider) :
    ViewModel() {

    /**
     * Triggers the delete action
     */
    fun triggerDelete() {
        historyInfoTopBarHandler.triggerDeleteHistoryWorkout()
    }

    /**
     * Triggers the save action
     */
    fun triggerSave() {
        historyInfoTopBarHandler.triggerSaveAsTemplate()
    }
}