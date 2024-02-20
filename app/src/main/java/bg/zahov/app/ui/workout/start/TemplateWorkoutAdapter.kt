package bg.zahov.app.ui.workout.start

import android.view.View
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class TemplateWorkoutAdapter : BaseAdapter<Workout>(
    areItemsTheSame = { oldItem, newItem -> oldItem.name == newItem.name },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_template
) {
    var itemClickListener: ItemClickListener<Workout>? = null

    override fun createViewHolder(view: View): WorkoutAdapterViewHolder =
        WorkoutAdapterViewHolder(view)

    inner class WorkoutAdapterViewHolder(view: View) : BaseViewHolder<Workout>(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.workout_title)
        private val lastPerformed = view.findViewById<MaterialTextView>(R.id.last_performed)
        private val settings = view.findViewById<ShapeableImageView>(R.id.settings)
        private val exercises = view.findViewById<MaterialTextView>(R.id.exercises)

        override fun bind(item: Workout) {
            title.text = item.name
            lastPerformed.text = "Last performed: ${item.date}"
            settings.setOnClickListener {
                itemClickListener?.onSettingsClicked(item, settings)
            }

            itemView.setOnClickListener {
                itemClickListener?.onWorkoutClicked(item, itemView)
            }
            if (item.isTemplate) {
                exercises.text =
                    item.exercises.joinToString("\n") {
                        "${if (it.sets.isNotEmpty()) "${it.sets.size} X " else ""}${it.name} "
                    }
            }
        }
    }

    interface ItemClickListener<T> {
        fun onSettingsClicked(item: T, clickedView: View)
        fun onWorkoutClicked(item: T, clickedView: View)
    }
}