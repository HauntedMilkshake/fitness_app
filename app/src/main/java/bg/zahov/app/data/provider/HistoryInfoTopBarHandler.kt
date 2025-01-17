package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.HistoryInfoActionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryInfoTopBarHandler() : HistoryInfoActionHandler {

    companion object {
        /**
         * Singleton instance of [HistoryInfoTopBarHandler]
         */
        private var instance: HistoryInfoTopBarHandler? = null

        /**
         * Provides the singleton instance of [HistoryInfoTopBarHandler]. If the instance is not yet created,
         * a new instance will be created and returned.
         *
         * @return The singleton instance of [HistoryInfoTopBarHandler].
         */
        fun getInstance() = instance ?: HistoryInfoTopBarHandler().also { instance = it }

    }

    private val _shouldSave = MutableStateFlow(false)
    /**
     * A [StateFlow] indicating whether we should save the workout
     * as a template
     */
    override val shouldSave: StateFlow<Boolean> = _shouldSave

    private val _shouldDelete = MutableStateFlow(false)

    /**
     * A [StateFlow] indicating whether we should delete the workout from
     * history
     */
    override val shouldDelete: StateFlow<Boolean> = _shouldDelete


    /**
     * Signals to save by setting [shouldSave] to 'true'
     */
    override fun triggerSave() {
        _shouldSave.value = true
    }

    /**
     * Signals to delete by setting [shouldDelete] to 'true'
     */
    override fun triggerDelete() {
        _shouldDelete.value = true
    }

    /**
     * Resets [shouldSave]
     */
    override fun resetSave() {
        _shouldSave.value = false
    }

    /**
     * Resets [shouldDelete]
     */
    override fun resetDelete() {
        _shouldDelete.value = false
    }
}