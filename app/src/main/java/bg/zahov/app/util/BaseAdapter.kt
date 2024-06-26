package bg.zahov.app.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(
    private val areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val areContentsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val layoutResId: Int,
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder<T>>() {

    private val items = ArrayList<T>()

    abstract fun createViewHolder(view: View): BaseViewHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return createViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
    open fun updateItems(newItems: List<T>) {
        val result = DiffUtil.calculateDiff(
            GenericDiffUtil(
                oldList = items,
                newList = newItems,
                areItemsTheSame = areItemsTheSame,
                areContentsTheSame = areContentsTheSame
            )
        )
        items.clear()
        items.addAll(newItems)
        result.dispatchUpdatesTo(this)

    }
    fun getRecyclerViewItems() = items

    abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }
}
