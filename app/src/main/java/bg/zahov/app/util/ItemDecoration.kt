package bg.zahov.app.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(
    private val left: Int,
    private val right: Int,
    private val top: Int,
    private val bottom: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
    ) {
        outRect.apply {
            top = this@SpacingItemDecoration.top
            left = this@SpacingItemDecoration.left
            right = this@SpacingItemDecoration.right
            bottom = this@SpacingItemDecoration.bottom
        }
    }
}
