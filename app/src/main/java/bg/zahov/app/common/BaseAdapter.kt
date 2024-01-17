package bg.zahov.app.common

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

// FIXME check whether androidx.recyclerview.widget.ListAdapter will fit your  needs

abstract class BaseAdapter<T>(
    private val areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val areContentsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val layoutResId: Int,
) : RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder>() {

    private val items = ArrayList<T>()
    private var diffUtil: GenericDiffUtil<T>? = null

    init {
        // FIXME this instantiation does not make sense
        diffUtil = GenericDiffUtil(
            oldList = listOf(),
            newList = listOf(),
            areItemsTheSame = areItemsTheSame,
            areContentsTheSame = areContentsTheSame
        )
    }

    abstract fun createViewHolder(view: View): BaseViewHolder

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return createViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<T>) {
        // FIXME what is this null check about? The checked object is never used
        val oldItems = items
        diffUtil?.let {
            val result = DiffUtil.calculateDiff(
                GenericDiffUtil(
                    oldList = oldItems,
                    newList = newItems,
                    areItemsTheSame = areItemsTheSame,
                    areContentsTheSame = areContentsTheSame
                )
            )
            Log.d("DIFF", "update items old items size ${items.size}")
            items.clear()
            items.addAll(newItems)
            Log.d("DIFF", "update items new items size ${items.size}")
            // FIXME again, this is not used and redundant
            diffUtil = GenericDiffUtil(
                oldList = oldItems,
                newList = items,
                areItemsTheSame = areItemsTheSame,
                areContentsTheSame = areContentsTheSame
            )
            result.dispatchUpdatesTo(this)
        }
    }

    internal fun deleteItem(position: Int) {
        val new = items
        new.removeAt(position)
        updateItems(new)
    }

    internal fun addItem(item: T) {
        Log.d("DIFF", "items old size - ${items.size}")
        val new = items
        new.add(item)
        Log.d("DIFF", "items new size - ${new.size}")
        updateItems(new)
    }

    // FIXME this doesn't need to be an inner class, make it abstract with abstract method bind and
    //  make it generic
    open inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(item: T) {

        }
    }

    // FIXME I believe you want to grant access to items to subclasses, make items protected
    //  or make this method protected and set the return type to List<T> to hide collection mutability
    internal fun getItems() = items
}
