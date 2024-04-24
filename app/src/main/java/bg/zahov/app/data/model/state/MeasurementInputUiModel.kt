package bg.zahov.app.data.model.state

import bg.zahov.app.ui.measures.info.input.MeasurementInputViewModel

data class MeasurementInputUiModel(
    val name: String = "",
    val date: String = "",
    val message: String = "",
    val action: Boolean = false,
)

object MeasurementInputUiMapper {
    fun map(state: MeasurementInputViewModel.State) = when (state) {
        is MeasurementInputViewModel.State.Measurement -> MeasurementInputUiModel(
            name = state.name,
            date = state.date
        )

        is MeasurementInputViewModel.State.Navigate -> MeasurementInputUiModel(action = state.action)
        is MeasurementInputViewModel.State.Notify -> MeasurementInputUiModel(
            name = state.name,
            date = state.date,
            message = state.message
        )
    }
}