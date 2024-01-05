package bg.zahov.app.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.realm_db.Workout
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class WorkoutAdapter: RecyclerView.Adapter<WorkoutAdapter.WorkoutAdapterViewHolder>() {
    private val items = ArrayList<Workout>()
    var itemClickListener: ItemClickListener<Workout>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutAdapterViewHolder {
        return WorkoutAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.template_item, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: WorkoutAdapterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(newWorkouts: List<Workout>){
        items.clear()
        items.addAll(newWorkouts)
        notifyDataSetChanged()
    }

    inner class WorkoutAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val title = view.findViewById<MaterialTextView>(R.id.workout_title)
        private val lastPerformed = view.findViewById<MaterialTextView>(R.id.last_performed)
        private val settings = view.findViewById<ShapeableImageView>(R.id.settings)
        private val sets = view.findViewById<MaterialTextView>(R.id.sets)
        private val layout = view.findViewById<ConstraintLayout>(R.id.template_layout)

        fun bind(workout: Workout){
            title.text = workout.workoutName
            lastPerformed.text = workout.date
            settings.setOnClickListener {
                itemClickListener?.onItemClicked(workout, settings)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, clickedView: View)
    }
}