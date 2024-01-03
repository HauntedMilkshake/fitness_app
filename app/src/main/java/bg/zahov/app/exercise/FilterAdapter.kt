package bg.zahov.app.exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.Filter
import bg.zahov.app.realm_db.Exercise
import bg.zahov.app.utils.equalsTo
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class FilterAdapter(private val isRemovable: Boolean): RecyclerView.Adapter<FilterAdapter.FilterAdapterViewHolder>() {

    private val items = ArrayList<Filter>()
    var itemClickListener: ItemClickListener<Filter>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterAdapterViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return FilterAdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilterAdapterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newFilters: List<Filter>) {
        val oldList = ArrayList(items)

        items.clear()
        items.addAll(newFilters)

        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newFilters.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newFilters[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].equals(newFilters[newItemPosition])
            }
        }).dispatchUpdatesTo(this)
    }
    fun initItems(initItems: List<Filter>){
        items.addAll(initItems)
    }

    inner class FilterAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val layout = view.findViewById<ConstraintLayout>(R.id.filter_item_layout)
        private val filterView = view.findViewById<MaterialTextView>(R.id.text)
        private val remove = view.findViewById<ShapeableImageView>(R.id.remove)

        fun bind(filter: Filter) {
            filterView.text = filter.name
            remove.visibility = if(isRemovable) View.VISIBLE else View.GONE
            layout.setBackgroundResource(if(filter.selected) R.drawable.filter_item_clicked else R.drawable.filter_item_unclicked)
            layout.setOnClickListener {
                filter.apply{
                    if(selected){
                        it.setBackgroundResource(R.drawable.filter_item_unclicked)
                        selected = false
                    }else{
                        it.setBackgroundResource(R.drawable.filter_item_clicked)
                        selected = true
                    }
                }
               itemClickListener?.onItemClicked(filter, layout)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, clickedView: View)
    }
}