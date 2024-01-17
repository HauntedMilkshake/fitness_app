package bg.zahov.app.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

//FIXME this is UI-related component move it to an more suitable package. also use better field names
// it's clear that this is spacing, just use left, top, right and bottom.
class SpacingItemDecoration(
    private val lSpacing: Int,
    private val rSpacing: Int,
    private val tSpacing: Int,
    private val bSpacing: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
    ) {
        outRect.apply {
            top = tSpacing
            left = lSpacing
            right = rSpacing
            bottom = bSpacing
        }
    }
}
