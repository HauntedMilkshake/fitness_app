package bg.zahov.app.ui.history

import android.view.View
import bg.zahov.app.data.model.Units
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.BaseAdapter
import bg.zahov.app.util.timeToString
import bg.zahov.app.util.toFormattedString
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class HistoryAdapter(private val units: Units = Units.Metric) : BaseAdapter<Workout>(
    areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_past_workout
) {

    var itemClickListener: ItemClickListener<Workout>? = null
    override fun createViewHolder(view: View): WorkoutAdapterViewHolder =
        WorkoutAdapterViewHolder(view)

    inner class WorkoutAdapterViewHolder(view: View) : BaseViewHolder<Workout>(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.workout_name)
        private val date = view.findViewById<MaterialTextView>(R.id.workout_date)
        private val duration = view.findViewById<MaterialTextView>(R.id.duration)
        private val volume = view.findViewById<MaterialTextView>(R.id.volume)
        private val prs = view.findViewById<MaterialTextView>(R.id.pr_count)
        private val exercises = view.findViewById<MaterialTextView>(R.id.exercises)
        private val bestSets = view.findViewById<MaterialTextView>(R.id.best_sets)

        override fun bind(item: Workout) {
            title.text = item.name
            date.text = item.date.toFormattedString()
            duration.text = item.duration?.timeToString()
            volume.text = "${item.volume ?: 0} ${if (units == Units.Metric) "kg" else "lbs"}"
            prs.text = item.personalRecords.toString()
            exercises.text = item.exercises.joinToString("\n") {
                "${if (it.sets.isNotEmpty()) "${it.sets.size} x " else ""}${it.name} "
            }
            bestSets.text = item.exercises.joinToString("\n") {
                "${it.bestSet.firstMetric ?: 0} x ${it.bestSet.secondMetric ?: 0}"
            }
            itemView.setOnClickListener {
                itemClickListener?.onWorkoutClick(item, bindingAdapterPosition)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onWorkoutClick(item: T, position: Int)
    }
}

