package bg.zahov.app.ui.exercise

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter : BaseAdapter<ExerciseAdapterWrapper>(
    areItemsTheSame = { oldItem, newItem -> oldItem.name == newItem.name },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_exercise
) {
    var itemClickListener: ItemClickListener<ExerciseAdapterWrapper>? = null
    override fun createViewHolder(view: View): BaseViewHolder<ExerciseAdapterWrapper> =
        ExerciseAdapterViewHolder(view)

    inner class ExerciseAdapterViewHolder(view: View) :
        BaseViewHolder<ExerciseAdapterWrapper>(view) {
        private val exerciseBackground =
            view.findViewById<ConstraintLayout>(R.id.exercise_background)
        private val exerciseImage = view.findViewById<ShapeableImageView>(R.id.exercise_image)
        private val exerciseTitle = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val exerciseSubtitle = view.findViewById<MaterialTextView>(R.id.body_part)

        override fun bind(item: ExerciseAdapterWrapper) {
            exerciseTitle.text = item.name
            exerciseSubtitle.text = item.bodyPart
            exerciseImage.setImageResource(item.imageResource)
            exerciseBackground.setBackgroundResource(item.backgroundResource)

            itemView.setOnClickListener {
                itemClickListener?.onItemClicked(item, bindingAdapterPosition)
                notifyDataSetChanged()
            }
        }

    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, position: Int)
    }
}

data class ExerciseAdapterWrapper(
    val name: String,
    val bodyPart: String,
    val category: String,
    var imageResource: Int,
    var backgroundResource: Int,
)