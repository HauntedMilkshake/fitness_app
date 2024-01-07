package bg.zahov.app.exercise

import android.view.View
import bg.zahov.app.common.BaseAdapter
import bg.zahov.app.data.Filter
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class FilterAdapter(private val isRemovable: Boolean) : BaseAdapter<Filter>(
    areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
    areContentsTheSame = { oldItem, newItem -> oldItem.equalsTo(newItem) },
    layoutResId = R.layout.filter_item
) {

    internal var itemClickListener: ItemClickListener<Filter>? = null

    inner class FilterAdapterViewHolder(view: View) : BaseViewHolder(view) {
        private val filterView = view.findViewById<MaterialTextView>(R.id.text)
        private val remove = view.findViewById<ShapeableImageView>(R.id.remove)

        override fun bind(filter: Filter) {
            filterView.text = filter.name
            remove.visibility = if (isRemovable) View.VISIBLE else View.GONE
            itemView.setBackgroundResource(if (filter.selected) R.drawable.filter_item_clicked else R.drawable.filter_item_unclicked)

            itemView.setOnClickListener {
                filter.apply {
                    it.setBackgroundResource(if (selected) R.drawable.filter_item_unclicked else R.drawable.filter_item_clicked)
                    selected = !selected
                    itemClickListener?.onItemClicked(this, itemView)
                }
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, clickedView: View)
    }

    override fun createViewHolder(view: View): BaseViewHolder = FilterAdapterViewHolder(view)
}