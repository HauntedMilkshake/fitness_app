package bg.zahov.app.ui.measures

import android.view.View
import bg.zahov.app.util.BaseAdapter
import bg.zahov.fitness.app.R
import com.google.android.material.textview.MaterialTextView

class MeasurementsAdapter : BaseAdapter<AdapterMeasurement>(
    areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
    areContentsTheSame = { oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_measuerment_item
) {
    var itemClickListener: ItemClickListener<AdapterMeasurement>? = null
    override fun createViewHolder(view: View): BaseViewHolder<AdapterMeasurement> =
        MeasurementsAdapter(view)

    inner class MeasurementsAdapter(view: View) : BaseViewHolder<AdapterMeasurement>(view) {
        private val title = view.findViewById<MaterialTextView>(R.id.measurement_title)
        override fun bind(item: AdapterMeasurement) {
            title.text = item.title

            itemView.setOnClickListener {
                itemClickListener?.onItemClicked(item, adapterPosition)
            }
        }
    }

    interface ItemClickListener<T> {
        fun onItemClicked(item: T, position: Int)
    }
}

data class AdapterMeasurement(val title: String)