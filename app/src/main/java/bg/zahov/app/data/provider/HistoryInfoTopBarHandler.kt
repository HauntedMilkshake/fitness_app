package bg.zahov.app.data.provider

import bg.zahov.app.data.interfaces.HistoryInfoActionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HistoryInfoTopBarHandler() : HistoryInfoActionHandler {

    companion object {
        private var instance: HistoryInfoTopBarHandler? = null

        fun getInstance() = instance ?: HistoryInfoTopBarHandler().also { instance = it }

    }

    private val _shouldSave = MutableStateFlow(false)
    override val shouldSave: StateFlow<Boolean> = _shouldSave

    private val _shouldDelete = MutableStateFlow(false)
    override val shouldDelete: StateFlow<Boolean> = _shouldDelete


    override fun triggerSave() {
        _shouldSave.value = true
    }


    override fun triggerDelete() {
        _shouldDelete.value = true
    }

    override fun resetSave() {
        _shouldSave.value = false
    }

    override fun resetDelete() {
        _shouldDelete.value = false
    }
}