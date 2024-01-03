package bg.zahov.app.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(private val lSpacing: Int, private val rSpacing: Int, private val tSpacing: Int, private val bSpacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.apply {
            top = tSpacing
            left = lSpacing
            right = rSpacing
            bottom = bSpacing
        }
    }
}
