package bg.zahov.app.ui.workout.add

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.ExerciseWithNoteVisibility
import bg.zahov.app.data.model.SelectableExercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
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
    var textChangeListener: TextActionListener? = null
//    var swipeGesture: SwipeGesture? = null

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
                holder.bind((items[position] as SetEntry).setEntry)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ExerciseWithNoteVisibility>) {
        val oldList = items
        val workoutEntry = mutableListOf<WorkoutEntry>()
        newItems.forEach { item ->
            workoutEntry.add(ExerciseEntry(item))
            item.exercise.sets.forEach { set ->
                workoutEntry.add(SetEntry(ClickableSet(set, false)))
            }
        }
        items.clear()
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
//                    oldList[oldItemPosition] is SetEntry && items[newItemPosition] is SetEntry -> {
//                        (oldList[oldItemPosition] as SetEntry).set.set == (items[newItemPosition] as SetEntry).set.set
//                    }
//
//                    else -> {
//                        false
//                    }
//                }
//            }
//
//            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
//                oldList[oldItemPosition] == items[newItemPosition]
//        }).dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

    inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val options = view.findViewById<ShapeableImageView>(R.id.options)
        private val note = view.findViewById<TextInputLayout>(R.id.workout_note)
        private val addSetButton = view.findViewById<MaterialButton>(R.id.add_set)
        private val firstInputColumnIndicator =
            view.findViewById<MaterialTextView>(R.id.first_input_column_indicator)
        private val secondInputColumnIndicator =
            view.findViewById<MaterialTextView>(R.id.second_input_column_indicator)

        fun bind(item: ExerciseEntry) {
            title.text = item.exerciseEntry.exercise.name
            firstInputColumnIndicator.text = when (item.exerciseEntry.exercise.category) {
                Category.AssistedWeight -> "-KG"
                Category.RepsOnly -> "REPS"
                Category.Cardio -> "DURATION"
                Category.Timed -> "DURATION"
                else -> "+KG"
            }
            secondInputColumnIndicator.visibility = when (item.exerciseEntry.exercise.category) {
                Category.RepsOnly -> View.GONE
                Category.Cardio -> View.GONE
                Category.Timed -> View.GONE
                else -> View.VISIBLE
            }

            note.visibility = if (item.exerciseEntry.noteVisibility) View.VISIBLE else View.GONE

            options.setOnClickListener {
                showExerciseMenu(item.exerciseEntry, it)
//                itemClickListener?.onOptionsClicked(item.exerciseEntry, it)
            }

            addSetButton.setOnClickListener {
                itemClickListener?.onAddSet(
                    item.exerciseEntry,
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

        fun bind(item: ClickableSet) {
            setIndicator.text = "$adapterPosition"
            previous.text = "-" //TODO()
            secondInputLayout.visibility = when (getExerciseForSet()?.exercise?.category) {
                Category.RepsOnly -> View.GONE
                Category.Cardio -> View.GONE
                Category.Timed -> View.GONE
                else -> View.VISIBLE

            }
            setIndicator.setOnClickListener {
                getExerciseForSet()?.let { exercise ->
                    showSetMenu(exercise, item.set, it)
                }
            }
            check.setOnClickListener {
                itemClickListener?.onSetCheckClicked(item, it)
                //TODO(Change background and play dopamine inducing animation)
            }
            firstInputEditText.addTextChangedListener {
                Log.d("TEXT CHANGED", "text changed on first field")
                getExerciseForSet()?.let { exercise ->
                    Log.d("Calling text changed", "calling text changed")
                    textChangeListener?.onInputFieldChanged(
                        exercise,
                        item,
                        it.toString(),
                        firstInputEditText.id
                    )
                }
            }
            secondInputEditText.addTextChangedListener {
                getExerciseForSet()?.let { exercise ->
                    textChangeListener?.onInputFieldChanged(
                        exercise,
                        item,
                        it.toString(),
                        secondInputEditText.id
                    )
                }
            }
        }


        fun deleteSet() {
            for (i in adapterPosition downTo 0) {
                if (items[i] is ExerciseEntry) {
                    swipeActionListener?.onDeleteSet(
                        (items[i] as ExerciseEntry).exerciseEntry,
                        (items[adapterPosition] as SetEntry).setEntry
                    )
                }
            }
        }

        private fun getExerciseForSet(): ExerciseWithNoteVisibility? {
            for (i in adapterPosition downTo 0) {
                if (items[i] is ExerciseEntry) {
                    return (items[i] as ExerciseEntry).exerciseEntry
                }
            }
            return null
        }
    }


    interface ItemClickListener<T> {
        fun onSetCheckClicked(item: ClickableSet, clickedView: View)
        fun onAddSet(item: ExerciseWithNoteVisibility, set: ClickableSet)
        fun onNoteToggle(item: ExerciseWithNoteVisibility)
        fun onReplaceExercise(item: ExerciseWithNoteVisibility)
        fun onRemoveExercise(item: ExerciseWithNoteVisibility)
        fun onSetTypeChanged(item: ExerciseWithNoteVisibility, set: Sets, setType: SetType)
    }

    interface SwipeActionListener {
        fun onDeleteSet(item: ExerciseWithNoteVisibility, set: ClickableSet)
    }

    interface TextActionListener {
        fun onInputFieldChanged(
            exercise: ExerciseWithNoteVisibility,
            set: ClickableSet,
            metric: String,
            id: Int
        )
    }

    private fun showExerciseMenu(exercise: ExerciseWithNoteVisibility, view: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(view.context, R.style.MyPopUp), view)
        popupMenu.menuInflater.inflate(R.menu.popup_exercise_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add_note -> {
                    itemClickListener?.onNoteToggle(exercise)
                }

                R.id.action_replace -> {
                    itemClickListener?.onReplaceExercise(exercise)
                }

                R.id.action_remove -> {
                    itemClickListener?.onRemoveExercise(exercise)
                }
            }
            true
        }
        popupMenu.show()
    }

    private fun showSetMenu(exercise: ExerciseWithNoteVisibility, set: Sets, view: View) {
        val popupMenu = PopupMenu(ContextThemeWrapper(view.context, R.style.MyPopUp), view)
        popupMenu.menuInflater.inflate(R.menu.popup_set_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_drop_set -> {
                    itemClickListener?.onSetTypeChanged(exercise, set, SetType.DROP_SET)
                }

                R.id.action_failure_set -> {
                    itemClickListener?.onSetTypeChanged(exercise, set, SetType.FAILURE)
                }

                R.id.action_warmup_set -> {
                    itemClickListener?.onSetTypeChanged(exercise, set, SetType.WARMUP)
                }

                else -> {
                    itemClickListener?.onSetTypeChanged(exercise, set, SetType.DEFAULT)
                }
            }
            true
        }
        popupMenu.show()
    }

}

sealed class WorkoutEntry

data class ExerciseEntry(var exerciseEntry: ExerciseWithNoteVisibility) : WorkoutEntry()

data class SetEntry(val setEntry: ClickableSet) : WorkoutEntry()