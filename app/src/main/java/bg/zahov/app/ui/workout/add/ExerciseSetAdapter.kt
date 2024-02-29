package bg.zahov.app.ui.workout.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
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
                holder.bind((items[position] as ExerciseEntry).exerciseEntry)
            }

            is SetViewHolder -> {
                holder.bind((items[position] as SetEntry).setEntry)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<WorkoutEntry>) {
//        val oldList = items
        items.clear()
        items.addAll(newItems)

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
        private val noteLayout = view.findViewById<TextInputLayout>(R.id.workout_note)
        private val noteEditText = view.findViewById<TextInputEditText>(R.id.workout_note_text)
        private val addSetButton = view.findViewById<MaterialButton>(R.id.add_set)
        private val firstInputColumnIndicator =
            view.findViewById<MaterialTextView>(R.id.first_input_column_indicator)
        private val secondInputColumnIndicator =
            view.findViewById<MaterialTextView>(R.id.second_input_column_indicator)

        fun bind(item: ExerciseSetAdapterExerciseWrapper) {
            title.text = item.name
            firstInputColumnIndicator.apply {
                visibility = item.firstInputColumnVisibility
                setText(item.firstInputColumnResource)
            }
            secondInputColumnIndicator.apply {
                visibility = item.secondInputColumnVisibility
                setText(item.secondInputColumnResource)
            }
            noteLayout.visibility = item.noteVisibility
            noteEditText.setText(item.note)

            //item.exerciseEntry.name
//            firstInputColumnIndicator.text = when (item.exerciseEntry.category) {
//                Category.AssistedWeight -> "-KG"
//                Category.RepsOnly -> "REPS"
//                Category.Cardio -> "DURATION"
//                Category.Timed -> "DURATION"
//                else -> "+KG"
//            }
//            secondInputColumnIndicator.visibility = when (item.exerciseEntry.category) {
//                Category.RepsOnly -> View.GONE
//                Category.Cardio -> View.GONE
//                Category.Timed -> View.GONE
//                else -> View.VISIBLE
//            }

            options.setOnClickListener {
                showExerciseMenu(adapterPosition, adapterPosition, it)
            }

            addSetButton.setOnClickListener {
                itemClickListener?.onAddSet(adapterPosition)
            }
        }

        private fun showExerciseMenu(
            exerciseId: Int,
            itemPosition: Int,
            view: View,
        ) {
            val popupMenu = PopupMenu(ContextThemeWrapper(view.context, R.style.MyPopUp), view)
            popupMenu.menuInflater.inflate(R.menu.popup_exercise_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_add_note -> {
                        itemClickListener?.onNoteToggle(itemPosition)
                    }

                    R.id.action_replace -> {
                        itemClickListener?.onReplaceExercise(exerciseId)
                    }

                    R.id.action_remove -> {
                        itemClickListener?.onRemoveExercise(exerciseId)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }


    inner class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //TODO(add id to layout and when check is clicked play animation)
        private val setIndicator = view.findViewById<MaterialTextView>(R.id.set_number)
        private val previous = view.findViewById<MaterialTextView>(R.id.previous)
        private val firstInputLayout = view.findViewById<TextInputLayout>(R.id.first_input_field)
        private val firstInputEditText =
            view.findViewById<TextInputEditText>(R.id.first_input_field_text)
        private val secondInputLayout = view.findViewById<TextInputLayout>(R.id.second_input_field)
        private val secondInputEditText =
            view.findViewById<TextInputEditText>(R.id.second_input_field_text)
        private val check = view.findViewById<ShapeableImageView>(R.id.check)

        fun bind(item: ExerciseSetAdapterSetWrapper) {
            previous.text = item.previousResults
            secondInputLayout.visibility = item.secondInputFieldVisibility
            setIndicator.apply {
                setText(item.setIndicatorResource)
                if (item.setIndicatorResource == R.string.default_set_indicator) text =
                    "${item.setNumber}"
            }
//            secondInputLayout.visibility = when (getExerciseForSet()?.category) {
//                Category.RepsOnly -> View.GONE
//                Category.Cardio -> View.GONE
//                Category.Timed -> View.GONE
//                else -> View.VISIBLE
//
//            }
            setIndicator.setOnClickListener {
                showSetMenu(adapterPosition, it)
            }

            check.setOnClickListener {
                itemClickListener?.onSetCheckClicked(adapterPosition)
            }

            firstInputEditText.addTextChangedListener {
                textChangeListener?.onInputFieldChanged(
                    adapterPosition,
                    it.toString(),
                    firstInputEditText.id
                )
            }
            secondInputEditText.addTextChangedListener {
                textChangeListener?.onInputFieldChanged(
                    adapterPosition,
                    it.toString(),
                    secondInputEditText.id
                )
            }
        }

//        fun deleteSet() {
//            for (i in adapterPosition downTo 0) {
//                if (items[i] is ExerciseEntry) {
//                    swipeActionListener?.onDeleteSet(
//                        (items[i] as ExerciseEntry).exerciseEntry,
//                        (items[adapterPosition] as SetEntry).setEntry
//                    )
//                }
//            }
//        }

//        private fun getExerciseForSet(): InteractableExerciseWrapper? {
//            for (i in adapterPosition downTo 0) {
//                if (items[i] is ExerciseEntry) {
//                    return (items[i] as ExerciseEntry).exerciseEntry
//                }
//            }
//            return null
//        }

        private fun showSetMenu(itemPosition: Int, clickedView: View) {
            val popupMenu =
                PopupMenu(ContextThemeWrapper(clickedView.context, R.style.MyPopUp), clickedView)
            popupMenu.menuInflater.inflate(R.menu.popup_set_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                itemClickListener?.onSetTypeChanged(
                    itemPosition,
                    when (item.itemId) {
                        R.id.action_drop_set -> {
                            SetType.DROP_SET
                        }

                        R.id.action_failure_set -> {
                            SetType.FAILURE
                        }

                        R.id.action_warmup_set -> {
                            SetType.WARMUP
                        }

                        else -> {
                            SetType.DEFAULT
                        }
                    }
                )
                true
            }
            popupMenu.show()
        }
    }


    interface ItemClickListener<T> {
        fun onSetCheckClicked(itemPosition: Int)
        fun onAddSet(itemPosition: Int)
        fun onNoteToggle(itemPosition: Int)
        fun onReplaceExercise(itemPosition: Int)
        fun onRemoveExercise(itemPosition: Int)
        fun onSetTypeChanged(itemPosition: Int, setType: SetType)
    }

    interface SwipeActionListener {
        fun onDeleteSet(itemPosition: Int)
    }

    interface TextActionListener {
        fun onInputFieldChanged(
            itemPosition: Int,
            metric: String,
            id: Int,
        )
    }
}

sealed class WorkoutEntry

data class ExerciseEntry(var exerciseEntry: ExerciseSetAdapterExerciseWrapper) : WorkoutEntry()

data class SetEntry(val setEntry: ExerciseSetAdapterSetWrapper) : WorkoutEntry()

data class ExerciseSetAdapterExerciseWrapper(
    val id: String,
    val name: String,
    var backgroundResource: Int,
    var firstInputColumnResource: Int,
    var secondInputColumnResource: Int,
    var firstInputColumnVisibility: Int = View.VISIBLE,
    var secondInputColumnVisibility: Int = View.VISIBLE,
    val noteVisibility: Int = View.GONE,
    val note: String? = null
)

data class ExerciseSetAdapterSetWrapper(
    var setIndicatorResource: Int = R.string.default_set_indicator,
    var firstInputFieldVisibility: Int = View.VISIBLE,
    var secondInputFieldVisibility: Int = View.VISIBLE,
    val setNumber: Int,
    val previousResults: String,
    val set: Sets,
    var backgroundResourcse: Int
)