package bg.zahov.app.data.model.state

import bg.zahov.app.ui.history.info.HistoryInfoData
import bg.zahov.app.ui.history.info.HistoryInfoViewModel

data class HistoryInfoUiModel (
    val workout: HistoryInfoData? = null,
    val message: String? = null,
    val shutdown: Boolean = false
)

object HistoryInfoUiMapper {
    fun map(state: HistoryInfoViewModel.State) = when(state) {
        is HistoryInfoViewModel.State.CriticalError -> HistoryInfoUiModel(shutdown = true)
        is HistoryInfoViewModel.State.Data -> HistoryInfoUiModel(workout = state.data)
        is HistoryInfoViewModel.State.Notify -> HistoryInfoUiModel(workout = state.data, message = state.message)
    }
}