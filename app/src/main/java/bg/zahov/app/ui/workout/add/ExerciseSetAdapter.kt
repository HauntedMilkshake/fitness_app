package bg.zahov.app.ui.workout.add

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.SwipeGesture
import bg.zahov.fitness.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class ExerciseSetAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_EXERCISE = 1
        const val VIEW_TYPE_SETS = 0
    }

    private val items = ArrayList<WorkoutEntry>()
    var itemClickListener: ItemClickListener<WorkoutEntry>? = null
    var swipeActionListener: SwipeActionListener? = null
    var swipeGesture: SwipeGesture? = null

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is ExerciseEntry -> {
                VIEW_TYPE_EXERCISE
            }

            is SetEntry -> {
                VIEW_TYPE_SETS
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EXERCISE -> {
                ExerciseViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_exercise_set, parent, false)
                )
            }

            VIEW_TYPE_SETS -> {
                SetViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false)
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ExerciseViewHolder -> {
                holder.bind(items[position] as ExerciseEntry)
            }

            is SetViewHolder -> {
                holder.bind((items[position] as SetEntry).set, items.count { it is SetEntry })
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<Exercise>) {
        val oldList = items
        items.clear()
        val workoutEntry = mutableListOf<WorkoutEntry>()
        newItems.forEach { exercise ->
            workoutEntry.add(ExerciseEntry(exercise))
            exercise.sets.forEach { set ->
                workoutEntry.add(SetEntry(ClickableSet(set, false)))
            }
        }
        items.addAll(workoutEntry)
//        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
//            override fun getOldListSize(): Int = oldList.size
//
//            override fun getNewListSize(): Int = items.size
//
//            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return when {
//                    oldList[oldItemPosition] is ExerciseEntry && items[newItemPosition] is ExerciseEntry -> {
//                        (oldList[oldItemPosition] as ExerciseEntry).exercise.name == (items[newItemPosition] as ExerciseEntry).exercise.name
//                    }
//
//                    else -> {
//                        false
//                    }
//                }
//            }
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//                return when {
//                    oldList[oldItemPosition] is ExerciseEntry && items[newItemPosition] is ExerciseEntry -> {
//                        (oldList[oldItemPosition] as ExerciseEntry).exercise == (items[newItemPosition] as ExerciseEntry).exercise
//                    }
//
//                    oldList[oldItemPosition] is SetEntry && items[newItemPosition] is SetEntry -> {
//                        (oldList[oldItemPosition] as SetEntry).set == (items[newItemPosition] as SetEntry).set
//                    }
//
//                    else -> {
//                        false
//                    }
//                }
//
//            }
//        }).dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val options = view.findViewById<ShapeableImageView>(R.id.options)
        private val addSetButton = view.findViewById<MaterialButton>(R.id.add_set)

        fun bind(item: ExerciseEntry) {
            title.text = item.exercise.name

            options.setOnClickListener {
                itemClickListener?.onOptionsClicked(item.exercise, it)
            }

            addSetButton.setOnClickListener {
                itemClickListener?.onAddSet(
                    item.exercise,
                    ClickableSet(Sets(type = SetType.DEFAULT.key, 0.0, 0), false)
                )
            }
        }
    }

    inner class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val setIndicator = view.findViewById<MaterialTextView>(R.id.set_number)
        private val previous = view.findViewById<MaterialTextView>(R.id.previous)
        private val firstInputLayout = view.findViewById<TextInputLayout>(R.id.first_input_field)
        private val firstInputEditText =
            view.findViewById<TextInputEditText>(R.id.first_input_field_text)
        private val secondInputLayout = view.findViewById<TextInputLayout>(R.id.second_input_field)
        private val secondInputEditText =
            view.findViewById<TextInputEditText>(R.id.second_input_field_text)
        private val check = view.findViewById<ShapeableImageView>(R.id.check)

        fun bind(item: ClickableSet, position: Int) {
            setIndicator.text = "$position"
            previous.text = "-" //TODO()
            setIndicator.setOnClickListener {
                itemClickListener?.onSetClicked(item, itemView)
                //TODO(Add animation where it drops down and you can change the set type)
            }
            check.setOnClickListener {
                itemClickListener?.onSetCheckClicked(item, itemView)
                //TODO(Change background and play dopamine inducing animation)
            }
        }

        fun deleteSet() {
            for (i in adapterPosition downTo 0) {
                if (items[i] is ExerciseEntry) {
                    Log.d("on swipe", "exericse found on swipe")
                    swipeActionListener?.onDeleteSet(
                        (items[i] as ExerciseEntry).exercise,
                        (items[adapterPosition] as SetEntry).set
                    )
                }
            }
        }
    }


    interface ItemClickListener<T> {
        fun onOptionsClicked(item: Exercise, clickedView: View)
        fun onSetClicked(item: ClickableSet, clickedView: View)
        fun onSetCheckClicked(item: ClickableSet, clickedView: View)
        fun onAddSet(item: Exercise, set: ClickableSet)
    }

    interface SwipeActionListener {
        fun onDeleteSet(item: Exercise, set: ClickableSet)
    }
}

sealed class WorkoutEntry

data class ExerciseEntry(var exercise: Exercise) : WorkoutEntry()

data class SetEntry(val set: ClickableSet) : WorkoutEntry()