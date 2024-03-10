package bg.zahov.app.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.ui.workout.add.ExerciseSetAdapter

class SetSwipeGesture : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (viewHolder is ExerciseSetAdapter.SetViewHolder) {
            viewHolder.deleteSet()
        }
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        if (viewHolder is ExerciseSetAdapter.ExerciseViewHolder) {
            return 0
        }
        return super.getSwipeDirs(recyclerView, viewHolder)
    }
}