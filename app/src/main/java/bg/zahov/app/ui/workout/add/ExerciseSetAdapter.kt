package bg.zahov.app.ui.workout.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.app.data.model.Exercise
import bg.zahov.app.data.model.SetType
import bg.zahov.app.data.model.Sets
import bg.zahov.app.util.SwipeGesture
import bg.zahov.fitness.app.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ExerciseSetAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(), SwipeGesture.OnSwipeListener {

    companion object {
        const val VIEW_TYPE_EXERCISE = 1
        const val VIEW_TYPE_SETS = 0
    }

    private val items = ArrayList<Exercise>()
    var itemClickListener: ItemClickListener<Exercise>? = null

    override fun getItemViewType(position: Int): Int {
        //nqmam ideq kak tova stava currently e prosto filler :)
        return if(position == 0) VIEW_TYPE_EXERCISE else VIEW_TYPE_SETS
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_EXERCISE -> {
                ExerciseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false))
            }
            VIEW_TYPE_SETS -> {
                SetViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_set, parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ExerciseViewHolder -> {
                holder.bind(items[position])
            }
            is SetViewHolder -> {
                items[position].sets.forEach {
                    holder.bind(ClickableSet(it, false))
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
    inner class ExerciseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.exercise_title)
        private val options = view.findViewById<ShapeableImageView>(R.id.options)
        private val addSetButton = view.findViewById<MaterialButton>(R.id.add_set)

        fun bind(item: Exercise) {
            title.text = item.name

            options.setOnClickListener {
                itemClickListener?.onOptionsClicked(item, it)
            }

            addSetButton.setOnClickListener {
                itemClickListener?.onAddSet(
                    adapterPosition,
                    ClickableSet(Sets(type = SetType.DEFAULT.key, 0.0, 0), false)
                )
            }
        }
    }

    inner class SetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val setIndicator = view.findViewById<MaterialTextView>(R.id.set_number)
        private val previous = view.findViewById<MaterialTextView>(R.id.previous)
        private val firstInput = view.findViewById<MaterialTextView>(R.id.first_input_field)
        private val secondInput = view.findViewById<MaterialTextView>(R.id.second_input_field)
        private val check = view.findViewById<ShapeableImageView>(R.id.check)

        fun bind(item: ClickableSet) {
            setIndicator.text = "$itemCount"
            previous.text = "" //TODO()
            setIndicator.setOnClickListener {
                itemClickListener?.onSetClicked(item, itemView)
                //TODO(Add animation where it drops down and you can change the set type)
            }
            check.setOnClickListener {
                itemClickListener?.onSetCheckClicked(item, itemView)
                //TODO(Change background and play dopamine inducing animation)
            }
        }
    }
    interface ItemClickListener<T> {
        fun onOptionsClicked(item: T, clickedView: View)
        fun onSetClicked(item: ClickableSet, clickedView: View)
        fun onSetCheckClicked(item: ClickableSet, clickedView: View)
        fun onAddSet(exercisePosition: Int, set: ClickableSet)
        fun onDeleteSet(exercisePosition: Int, setPosition: Int)
    }

    override fun onSwipe(position: Int) {
//        itemClickListener?.onDeleteSet(, position)
    }
}