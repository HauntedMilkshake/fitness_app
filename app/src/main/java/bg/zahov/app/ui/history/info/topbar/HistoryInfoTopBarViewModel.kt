package bg.zahov.app.ui.history.info.topbar

import android.util.Log
import androidx.lifecycle.ViewModel
import bg.zahov.app.Inject
import bg.zahov.app.data.interfaces.HistoryInfoActionHandler

class HistoryInfoTopBarViewModel(private val historyInfoTopBarHandler: HistoryInfoActionHandler = Inject.historyInfoTopAppHandler) :
    ViewModel() {
    fun triggerDelete() {
        historyInfoTopBarHandler.triggerDelete()
    }

    fun triggerSave() {
        historyInfoTopBarHandler.triggerSave()
    }
}