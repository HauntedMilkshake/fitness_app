package bg.zahov.app.exercise

import android.view.View
import bg.zahov.app.common.BaseAdapter
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter : BaseAdapter<Exercise>(
    areItemsTheSame = { oldItem, newItem -> oldItem._id.toHexString() == newItem._id.toHexString() },
    areContentsTheSame = { oldItem, newItem -> oldItem.equalsTo(newItem) },
    layoutResId = R.layout.exercise_item
) {
    var itemClickListener: ItemClickListener<Exercise>? = null
    inner class ExerciseAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val exerciseTitle = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val exerciseSubtitle = view.findViewById<MaterialTextView>(R.id.body_part)

        override fun bind(exercise: Exercise) {
            exerciseTitle.text = exercise.exerciseName
            exerciseSubtitle.text = exercise.bodyPart

            itemView.setOnClickListener {
                itemClickListener?.onItemClicked(exercise, adapterPosition, itemView)
            }
        }
    }


    interface ItemClickListener<T> {
        fun onItemClicked(item: T, itemPosition: Int, clickedView: View)
    }
}
