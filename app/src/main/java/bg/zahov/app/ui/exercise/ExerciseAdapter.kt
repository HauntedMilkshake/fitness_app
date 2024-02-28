package bg.zahov.app.ui.exercise

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.InteractableExerciseWrapper
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter(
    private val replaceable: Boolean
) : BaseAdapter<InteractableExerciseWrapper>(
    areItemsTheSame = { oldItem, newItem -> oldItem.name == newItem.name },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_exercise
) {
    var itemClickListener: ItemClickListener<InteractableExerciseWrapper>? = null
    override fun createViewHolder(view: View): BaseViewHolder<InteractableExerciseWrapper> =
        ExerciseAdapterViewHolder(view)

    inner class ExerciseAdapterViewHolder(view: View) : BaseViewHolder<InteractableExerciseWrapper>(view) {
        private val exerciseBackground =
            view.findViewById<ConstraintLayout>(R.id.exercise_background)
        private val exerciseImage = view.findViewById<ShapeableImageView>(R.id.exercise_image)
        private val exerciseTitle = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val exerciseSubtitle = view.findViewById<MaterialTextView>(R.id.body_part)

        override fun bind(item: InteractableExerciseWrapper) {
            exerciseTitle.text = item.name
            exerciseSubtitle.text = item.bodyPart.key
            exerciseImage.setImageResource(if (item.isSelected) R.drawable.ic_check else getIcon(item.bodyPart))
            exerciseBackground.setBackgroundResource(if (item.isSelected) R.color.selected else R.color.background)

            itemView.setOnClickListener {
                itemClickListener?.onItemClicked(item, adapterPosition)
                notifyItemChanged(adapterPosition)
                if(replaceable) deselectRemainingExercise(item)
            }
        }
        private fun getIcon(bodyPart: BodyPart) = when(bodyPart) {
            BodyPart.Core -> R.drawable.ic_abs
            BodyPart.Arms -> R.drawable.ic_arms
            BodyPart.Back -> R.drawable.ic_back
            BodyPart.Chest -> R.drawable.ic_chest
            BodyPart.Legs -> R.drawable.ic_legs
            BodyPart.Shoulders -> R.drawable.ic_shoulders
            else -> R.drawable.ic_olympic
        }
    }

    private fun deselectRemainingExercise(ignoreItem: InteractableExerciseWrapper) {
        getRecyclerViewItems().forEachIndexed { index, selectableExercise ->
            if (selectableExercise != ignoreItem && selectableExercise.isSelected) {
                notifyItemChanged(index)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, position: Int)
    }
}
