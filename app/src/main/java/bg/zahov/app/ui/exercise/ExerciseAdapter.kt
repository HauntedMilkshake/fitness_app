package bg.zahov.app.ui.exercise

import android.view.View
import bg.zahov.app.util.BaseAdapter
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.util.applySelectAnimation
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter(
    private val selectable: Boolean,
) : BaseAdapter<SelectableExercise>(
    areItemsTheSame = { oldItem, newItem -> oldItem.exercise.name == newItem.exercise.name },
    areContentsTheSame = { oldItem, newItem -> oldItem.exercise == newItem.exercise },
    layoutResId = R.layout.item_exercise
) {
    var itemClickListener: ItemClickListener<SelectableExercise>? = null
    override fun createViewHolder(view: View): BaseViewHolder<SelectableExercise> =
        ExerciseAdapterViewHolder(view)

    inner class ExerciseAdapterViewHolder(view: View) : BaseViewHolder<SelectableExercise>(view) {
        private val exerciseImage = view.findViewById<ShapeableImageView>(R.id.exercise_image)
        private val exerciseTitle = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val exerciseSubtitle = view.findViewById<MaterialTextView>(R.id.body_part)

        override fun bind(item: SelectableExercise) {
            exerciseTitle.text = item.exercise.name
            exerciseSubtitle.text = item.exercise.bodyPart.name
            //TODO(add actual image resources and determine which one for which exercise)
            exerciseImage.setImageResource(if (item.isSelected) R.drawable.ic_closed_lock else R.drawable.ic_check)
            itemView.setBackgroundResource(if (item.isSelected) R.color.selected else R.color.background)

            itemView.setOnClickListener {

                if(selectable) {
                    it.applySelectAnimation(
                        !item.isSelected,
                        R.color.selected,
                        R.color.background
                    )
                }

                itemClickListener?.onItemClicked(item)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T)
    }
}
