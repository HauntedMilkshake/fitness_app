package bg.zahov.app.workout.addWorkout

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.backend.Exercise
import bg.zahov.app.backend.Sets
import bg.zahov.app.common.BaseAdapter
import bg.zahov.app.data.ClickableSet
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseSetAdapter : BaseAdapter<Exercise>(
    areItemsTheSame = { oldItem, newItem -> oldItem._id.toHexString() == newItem._id.toHexString() },
    areContentsTheSame = { oldItem, newItem -> oldItem.equalsTo(newItem) },
    layoutResId = R.layout.exercise_set_item
) {

    var exerciseItemClickListener: ItemClickListener<Exercise>? = null
    override fun createViewHolder(view: View) = ExerciseAdapterViewHolder(view)

    inner class ExerciseAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val options = view.findViewById<ShapeableImageView>(R.id.options)
        private val sets = view.findViewById<RecyclerView>(R.id.set_recycler_view)
        private val addSetButton = view.findViewById<MaterialButton>(R.id.add_set)

        override fun bind(item: Exercise) {
            title.text = item.exerciseName
            options.setOnClickListener {
                exerciseItemClickListener?.onOptionsClicked(item, it)
            }

            val setAdapter = SetAdapter().apply {
                itemClickListener = object : SetAdapter.ItemClickListener<ClickableSet> {
                    override fun onSetNumberClicked(item: ClickableSet, clickedView: View) {
                        exerciseItemClickListener?.onSetNumberClicked(item, clickedView)
                    }

                    override fun onCheckClicked(item: ClickableSet, clickedView: View) {
                        exerciseItemClickListener?.onCheckClicked(item, clickedView)
                        //might be redundant we can alter it in the bind itself
                        item.clicked = !(item.clicked)
                    }
                }
            }

            sets.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = setAdapter
            }

            //EXPOSE TO UI AND OBSERVE LIVE DATA
            addSetButton.setOnClickListener {
                exerciseItemClickListener?.addSet(adapterPosition)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onOptionsClicked(item: T, clickedView: View)
        fun onSetNumberClicked(item: ClickableSet, clickedView: View)
        fun addSet(exercisePosition: Int)
        fun onCheckClicked(item: ClickableSet, clickedView: View)
    }
}