package bg.zahov.app.ui.workout.start

import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import bg.zahov.app.data.model.Workout
import bg.zahov.app.util.BaseAdapter
import bg.zahov.app.util.toFormattedString
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
            lastPerformed.text = "Last performed: ${item.date.toFormattedString()}"
            settings.setOnClickListener {
                showCustomLayout(item, bindingAdapterPosition, it)
            }

            itemView.setOnClickListener {
                itemClickListener?.onWorkoutClicked(item, itemView)
            }

            exercises.text =
                item.exercises.joinToString("\n") {
                    "${if (it.sets.isNotEmpty()) "${it.sets.size} x " else ""}${it.name} "
                }
        }
    }

    private fun showCustomLayout(workout: Workout, itemPosition: Int, view: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(view.context, R.style.MyPopUp), view)
        popupMenu.menuInflater.inflate(R.menu.menu_popup_workout, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_delete -> {
                    itemClickListener?.onWorkoutDelete(itemPosition)
                }

                R.id.action_duplicate -> {
                    itemClickListener?.onWorkoutDuplicate(itemPosition)
                }

                R.id.action_edit -> {
                    itemClickListener?.onWorkoutEdit(workout)

                }

                R.id.action_start_workout -> {
                    itemClickListener?.onWorkoutStart(itemPosition)
                }
            }
            true
        }
        popupMenu.show()
    }

    interface ItemClickListener<T> {
        fun onWorkoutStart(position: Int)
        fun onWorkoutDelete(position: Int)
        fun onWorkoutDuplicate(position: Int)
        fun onWorkoutEdit(item: T)
        fun onWorkoutClicked(item: T, clickedView: View)
    }
}