package bg.zahov.app.data.model.state

import android.opengl.Visibility
import android.view.View
import bg.zahov.app.ui.measures.info.MeasurementInfoViewModel
import com.github.mikephil.charting.data.Entry

data class MeasurementInformationUiModel(
    val loadingVisibility: Int = View.GONE,
    val chartVisibility: Int = View.VISIBLE,
    val maxData: Int? = null,
    val chartData: List<Entry> = listOf(),
    val shutdown: Boolean = false
)

object MeasurementInformationUiMapper {
    fun map(state: MeasurementInfoViewModel.State) = when(state) {
        is MeasurementInfoViewModel.State.Data -> MeasurementInformationUiModel(maxData = state.maxValue, chartData = state.entries)
        is MeasurementInfoViewModel.State.Loading -> MeasurementInformationUiModel(loadingVisibility = state.loadingVisibility, chartVisibility = View.GONE)
        else -> MeasurementInformationUiModel(shutdown = true)
    }
}