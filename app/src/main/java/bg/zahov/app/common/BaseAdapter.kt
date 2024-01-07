package bg.zahov.app.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T>(
    private val areItemsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val areContentsTheSame: (oldItem: T, newItem: T) -> Boolean,
    private val layoutResId: Int,
) : RecyclerView.Adapter<BaseAdapter<T>.BaseViewHolder>() {

    private val items = ArrayList<T>()
    private var diffUtil: GenericDiffUtil<T>? = null

    init {
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
        diffUtil?.let {
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
            diffUtil = GenericDiffUtil(
                oldList = items.toList(),
                newList = newItems.toList(),
                areItemsTheSame = areItemsTheSame,
                areContentsTheSame = areContentsTheSame
            )

            result.dispatchUpdatesTo(this)
        }
    }

    open inner class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        open fun bind(item: T) {

        }
    }

    internal fun getItems() = items
}
