package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.ui.measures.info.MeasurementInfoViewModel
import com.github.mikephil.charting.data.Entry

data class MeasurementInformationUiModel(
    val loadingVisibility: Int = View.GONE,
    val chartVisibility: Int = View.VISIBLE,
    val max: Float = 0f,
    val min: Float = 0f,
    val suffix: String = "",
    val chartData: List<Entry> = listOf()
)

object MeasurementInformationUiMapper {
    fun map(state: MeasurementInfoViewModel.State) = when(state) {
        is MeasurementInfoViewModel.State.Data -> MeasurementInformationUiModel(max = state.maxValue, min = state.minValue, suffix = state.suffix,  chartData = state.entries)
        is MeasurementInfoViewModel.State.Loading -> MeasurementInformationUiModel(loadingVisibility = state.loadingVisibility, chartVisibility = View.GONE)
    }
}