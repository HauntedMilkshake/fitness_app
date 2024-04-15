package bg.zahov.app.ui.workout.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.fitness.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView

class ExerciseSetAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_EXERCISE = 1
        const val VIEW_TYPE_SETS = 0
    }

    private val items = ArrayList<WorkoutEntry>()
    var itemClickListener: ItemClickListener<WorkoutEntry>? = null
    var swipeActionListener: SwipeActionListener? = null
    var textChangeListener: TextActionListener? = null

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
        items.clear()
        items.addAll(newItems)
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
                setText(item.secondInputColumnResource)
            }
            noteLayout.visibility = item.noteVisibility
            noteEditText.setText(item.note)
            noteEditText.doAfterTextChanged {
                textChangeListener?.onNoteChanged(bindingAdapterPosition, it.toString())
            }

            options.setOnClickListener {
                showExerciseMenu(bindingAdapterPosition, it)
            }

            addSetButton.setOnClickListener {
                itemClickListener?.onAddSet(bindingAdapterPosition)
            }
        }

        private fun showExerciseMenu(
            itemPosition: Int,
            view: View,
        ) {
            val popupMenu = PopupMenu(ContextThemeWrapper(view.context, R.style.MyPopUp), view)
            popupMenu.menuInflater.inflate(R.menu.menu_popup_exercise, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_add_note -> {
                        itemClickListener?.onNoteToggle(itemPosition)
                        notifyItemChanged(itemPosition)
                    }

                    R.id.action_replace -> {
                        itemClickListener?.onReplaceExercise(itemPosition)
                    }

                    R.id.action_remove -> {
                        itemClickListener?.onRemoveExercise(itemPosition)
                    }
                }
                true
            }
            popupMenu.show()
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

        fun bind(item: ExerciseSetAdapterSetWrapper) {
            previous.text = item.previousResults
            firstInputLayout.visibility = item.firstInputFieldVisibility
            secondInputLayout.visibility = item.secondInputFieldVisibility
            setIndicator.apply {
                setText(item.setIndicator)
                if (item.setIndicator == R.string.default_set_indicator) text = item.setNumber
                setOnClickListener {
                    showSetMenu(bindingAdapterPosition, it)
                }
            }

            firstInputEditText.apply {
                if ((item.set.firstMetric ?: 0.0) > 0.0) setText(item.set.firstMetric.toString())
                addTextChangedListener {
                    textChangeListener?.onInputFieldChanged(
                        bindingAdapterPosition,
                        it.toString(),
                        firstInputEditText.id
                    )
                }
            }

            secondInputEditText.apply {
                if ((item.set.secondMetric ?: 0) > 0) setText(item.set.secondMetric.toString())
                addTextChangedListener {
                    textChangeListener?.onInputFieldChanged(
                        bindingAdapterPosition,
                        it.toString(),
                        secondInputEditText.id
                    )
                }
            }
        }

        private fun showSetMenu(itemPosition: Int, clickedView: View) {
            val popupMenu =
                PopupMenu(ContextThemeWrapper(clickedView.context, R.style.MyPopUp), clickedView)
            popupMenu.menuInflater.inflate(R.menu.menu_popup_set, popupMenu.menu)
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

        fun deleteSet() {
            swipeActionListener?.onDeleteSet(bindingAdapterPosition)
            notifyItemRemoved(bindingAdapterPosition)
        }
    }

    interface ItemClickListener<T> {
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

        fun onNoteChanged(itemPosition: Int, text: String)
    }
}

sealed class WorkoutEntry

data class ExerciseEntry(var exerciseEntry: ExerciseSetAdapterExerciseWrapper) : WorkoutEntry()

data class SetEntry(val setEntry: ExerciseSetAdapterSetWrapper) : WorkoutEntry()

data class ExerciseSetAdapterExerciseWrapper(
    val name: String,
    var backgroundResource: Int,
    var firstInputColumnResource: Int,
    var secondInputColumnResource: Int,
    var firstInputColumnVisibility: Int = View.VISIBLE,
    var noteVisibility: Int = View.GONE,
    var note: String? = null,
    val bodyPart: BodyPart,
    val category: Category,
    val isTemplate: Boolean,
)

data class ExerciseSetAdapterSetWrapper(
    var setIndicator: Int = R.string.default_set_indicator,
    var firstInputFieldVisibility: Int = View.VISIBLE,
    var secondInputFieldVisibility: Int = View.VISIBLE,
    var setNumber: String,
    val previousResults: String,
    val set: Sets,
    var backgroundResource: Int = R.color.background,
)