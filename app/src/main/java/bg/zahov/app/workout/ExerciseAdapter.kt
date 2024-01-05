package bg.zahov.app.workout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class ExerciseAdapter: RecyclerView.Adapter<ExerciseAdapter.ExerciseAdapterViewHolder>() {
    private val items = ArrayList<Exercise>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseAdapterViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.template_exercise_item, parent, false)
        return ExerciseAdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseAdapterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newExercises: List<Exercise>) {
        val oldList: List<Exercise> = ArrayList(items)
        items.clear()
        items.addAll(newExercises)
        notifyDataSetChanged()
    }

    inner class ExerciseAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val exerciseLabel = view.findViewById<MaterialTextView>(R.id.exercise)

        fun bind(exercise: Exercise) {
            exerciseLabel.text = "${exercise.sets.size} x ${exercise.exerciseName}"
        }
    }

}