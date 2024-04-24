package bg.zahov.app.ui.exercise.filter

import android.view.View
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class FilterAdapter(private val removableVisibility: Int) : BaseAdapter<FilterWrapper>(
    areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_filter
) {

    internal var itemClickListener: ItemClickListener<FilterWrapper>? = null

    inner class FilterAdapterViewHolder(view: View) : BaseViewHolder<FilterWrapper>(view) {
        private val filterView = view.findViewById<MaterialTextView>(R.id.text)
        private val remove = view.findViewById<ShapeableImageView>(R.id.remove)

        override fun bind(item: FilterWrapper) {
            filterView.text = item.name
            remove.visibility = removableVisibility
            itemView.setBackgroundResource(item.backgroundResource)

            itemView.setOnClickListener {
                item.apply {
                    itemClickListener?.onItemClicked(this, itemView)
                }
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, clickedView: View)
    }

    override fun createViewHolder(view: View): BaseViewHolder<FilterWrapper> =
        FilterAdapterViewHolder(view)
}

data class FilterWrapper(val name: String, var backgroundResource: Int = R.drawable.filter_item_unclicked)