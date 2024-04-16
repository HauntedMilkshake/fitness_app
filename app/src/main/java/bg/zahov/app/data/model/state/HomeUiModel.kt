package bg.zahov.app.data.model.state

import android.view.View
import bg.zahov.app.ui.home.HomeViewModel
import com.github.mikephil.charting.data.BarEntry

data class HomeUiModel(
    val loadingVisibility: Int = View.VISIBLE,
    val chartVisibility: Int = View.GONE,
    val textFieldVisibility: Int = View.GONE,
    val numberOfWorkouts: Int = 0,
    val lineData: List<BarEntry> = listOf(),
    val weekRanges: List<String> = listOf(),
    val yMin: Float = 0f,
    val yMax: Float = 0f,
    val xMin: Float = 0f,
    val xMax: Float = 0f
)

object HomeUiMapper {
    fun map(state: HomeViewModel.State) = when (state) {
        HomeViewModel.State.Default -> HomeUiModel()
        is HomeViewModel.State.BarData -> HomeUiModel(
            loadingVisibility = View.GONE,
            chartVisibility = View.VISIBLE,
            textFieldVisibility = View.VISIBLE,
            numberOfWorkouts = state.numberOfWorkouts,
            lineData = state.chartData,
            weekRanges = state.weekRanges,
            yMin = state.yMin,
            yMax = state.yMax,
            xMin = state.xMin,
            xMax = state.xMax
        )
    }
}