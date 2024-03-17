package bg.zahov.app.ui.exercise.info.history

import android.util.Log
import android.view.View
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class ExerciseHistoryAdapter : BaseAdapter<ExerciseHistoryInfo>(
    areItemsTheSame = { oldItem, newItem -> oldItem.workoutId == newItem.workoutId },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_exercise_set_history
) {
    override fun createViewHolder(view: View): ExerciseHistoryAdapterViewHolder =
        ExerciseHistoryAdapterViewHolder(view)

    inner class ExerciseHistoryAdapterViewHolder(view: View) :
        BaseViewHolder<ExerciseHistoryInfo>(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.workout_name)
        private val lastPerformed = view.findViewById<MaterialTextView>(R.id.last_performed)
        private val sets = view.findViewById<MaterialTextView>(R.id.sets)
        private val oneRepMaxes = view.findViewById<MaterialTextView>(R.id.one_rep_max)
        override fun bind(item: ExerciseHistoryInfo) {
            title.text = item.workoutName
            lastPerformed.text = item.lastPerformed
            sets.text = item.sets.joinToString(separator = "\n") {
                "${it.secondMetric ?: 0} x ${it.firstMetric}"
            }
            oneRepMaxes.text = item.oneRepMaxes.joinToString("\n")
        }
    }
}

data class ExerciseHistoryInfo(
    val workoutId: String,
    val workoutName: String,
    val lastPerformed: String,
    val sets: List<Sets>,
    val oneRepMaxes: List<String>,
)
