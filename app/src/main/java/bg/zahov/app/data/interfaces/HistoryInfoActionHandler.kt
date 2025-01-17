package bg.zahov.app.data.interfaces

import kotlinx.coroutines.flow.StateFlow

interface HistoryInfoActionHandler {
    val shouldSave: StateFlow<Boolean>
    val shouldDelete: StateFlow<Boolean>
    fun triggerSave()
    fun triggerDelete()
    fun resetSave()
    fun resetDelete()
}