package bg.zahov.app.workout

import android.view.View
import bg.zahov.app.common.BaseAdapter
import bg.zahov.app.backend.Workout
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class WorkoutAdapter : BaseAdapter<Workout>(
    areItemsTheSame = { oldItem, newItem -> oldItem._id.toHexString() == newItem._id.toHexString() },
    areContentsTheSame = { oldItem, newItem -> oldItem.equalsTo(newItem) },
    layoutResId = R.layout.template_item
) {
    var itemClickListener: ItemClickListener<Workout>? = null

    override fun createViewHolder(view: View): WorkoutAdapterViewHolder =
        WorkoutAdapterViewHolder(view)

    inner class WorkoutAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.workout_title)
        private val lastPerformed = view.findViewById<MaterialTextView>(R.id.last_performed)
        private val settings = view.findViewById<ShapeableImageView>(R.id.settings)
        private val exercises = view.findViewById<MaterialTextView>(R.id.exercises)

        override fun bind(item: Workout) {
            title.text = item.workoutName
            lastPerformed.text = item.date
            settings.setOnClickListener {
                itemClickListener?.onSettingsClicked(item, settings)
            }
            itemView.setOnClickListener {
                itemClickListener?.onWorkoutClicked(item, itemView)
            }
            exercises.text =
                item.exercises.joinToString("\n") { "${it.sets.size} x ${it.exerciseName}" }
        }
    }

    interface ItemClickListener<T> {
        fun onSettingsClicked(item: T, clickedView: View)
        fun onWorkoutClicked(item: T, clickedView: View)
    }
}