package bg.zahov.app.ui.history.info

import android.view.View
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class HistoryInfoAdapter : BaseAdapter<HistoryInfo>(
    areItemsTheSame = { oldItem, newItem -> oldItem.exerciseName == oldItem.exerciseName },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_past_workout_info
) {
    override fun createViewHolder(view: View): HistoryInfoAdapterViewHolder =
        HistoryInfoAdapterViewHolder(view)

    inner class HistoryInfoAdapterViewHolder(view: View) : BaseViewHolder<HistoryInfo>(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.exercise_name)
        private val sets = view.findViewById<MaterialTextView>(R.id.sets)
        private val oneRepMaxes = view.findViewById<MaterialTextView>(R.id.one_rep_max)
        override fun bind(item: HistoryInfo) {
            title.text = item.exerciseName
            sets.text = item.sets.joinToString("\n") {
                "${it.secondMetric ?: 0} x ${it.firstMetric}"
            }
            oneRepMaxes.text = item.oneRepMaxes.joinToString("\n")
        }
    }
}

data class HistoryInfo(
    val exerciseName: String,
    val sets: List<Sets>,
    val oneRepMaxes: List<String>,
)