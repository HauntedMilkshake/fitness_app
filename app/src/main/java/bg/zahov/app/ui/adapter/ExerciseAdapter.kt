package bg.zahov.app.ui.adapter

import android.view.View
import bg.zahov.app.util.BaseAdapter
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.utils.applySelectAnimation
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter(
    private val selectable: Boolean,
) : BaseAdapter<SelectableExercise>(
    //FIXME This comment is FYI only, since you shouldn't use RealmObjects in your presentation, but ObjectId (alias for BsonId)
    // already has equals and hash code implemented, you can compare those directly
    areItemsTheSame = { oldItem, newItem -> oldItem.exercise._id.toHexString() == newItem.exercise._id.toHexString() },
    //FIXME see comment on those equalsTo extension functions in Extensions
    areContentsTheSame = { oldItem, newItem -> oldItem.exercise.equalsTo(newItem.exercise) },
    layoutResId = R.layout.exercise_item
) {
    var itemClickListener: ItemClickListener<SelectableExercise>? = null
    override fun createViewHolder(view: View): BaseViewHolder = ExerciseAdapterViewHolder(view)

    //FIXME I would rather pass the click listener to the bind method rather than use an inner class here,
    // as this way you have an implicit reference to the adapter in the ViewHolder
    inner class ExerciseAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val exerciseImage = view.findViewById<ShapeableImageView>(R.id.exercise_image)
        private val exerciseTitle = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val exerciseSubtitle = view.findViewById<MaterialTextView>(R.id.body_part)

        override fun bind(item: SelectableExercise) {
            exerciseTitle.text = item.exercise.exerciseName
            exerciseSubtitle.text = item.exercise.bodyPart
            itemView.setBackgroundResource(if (item.isSelected) R.color.selected else R.color.background)

            when (selectable) {
                true -> {
                    itemView.setOnClickListener {
                        exerciseImage.setImageResource(if (item.isSelected) R.drawable.ic_closed_lock else R.drawable.ic_check)
                        it.applySelectAnimation(
                            !item.isSelected,
                            R.color.selected,
                            R.color.background
                        )
                        it.setBackgroundResource(if (item.isSelected) R.color.selected else R.color.background)
                        item.isSelected = !item.isSelected
                        itemClickListener?.onItemClicked(item, adapterPosition, itemView)
                    }
                }

                false -> {
                    itemView.setOnClickListener {
                        itemClickListener?.onItemClicked(item, adapterPosition, itemView)
                    }
                }
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int, clickedView: View)
    }

    fun getSelected() = getItems().filter { it.isSelected }

}
