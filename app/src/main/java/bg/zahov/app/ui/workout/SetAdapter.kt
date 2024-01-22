package bg.zahov.app.ui.workout

import android.view.View
import bg.zahov.app.util.BaseAdapter
import bg.zahov.app.util.SwipeGesture
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class SetAdapter : BaseAdapter<ClickableSet>(
    areItemsTheSame = { oldItem, newItem -> oldItem == newItem },
    areContentsTheSame = {oldItem, newItem -> oldItem == newItem },
    layoutResId = R.layout.item_set
), SwipeGesture.OnSwipeListener
{
    var itemClickListener: ItemClickListener<ClickableSet>? = null
    override fun createViewHolder(view: View) = SetAdapterViewHolder(view)

    inner class SetAdapterViewHolder(view: View) : BaseViewHolder<ClickableSet>(view) {
        private val setIndicator = view.findViewById<MaterialTextView>(R.id.set_number)
        private val previous = view.findViewById<MaterialTextView>(R.id.previous)
        private val firstInput by lazy {
            view.findViewById<MaterialTextView>(R.id.first_input_field)
        }
        private val secondInput by lazy {
            view.findViewById<MaterialTextView>(R.id.second_input_field)
        }
        private val check = view.findViewById<ShapeableImageView>(R.id.check)

        override fun bind(item: ClickableSet) {
//            setIndicator.text = "${getItems().size}"
            // previous.text = TODO()

            setIndicator.setOnClickListener {
                itemClickListener?.onSetNumberClicked(item, itemView)
            }
            check.setOnClickListener {
                itemClickListener?.onCheckClicked(item, itemView)
                //TODO(Change background and play dopamine inducing animation)
            }
        }
    }

    interface ItemClickListener<T> {

        fun onSetNumberClicked(item: T, clickedView: View)
        fun onCheckClicked(item: T, clickedView: View)
    }

    override fun onSwipe(position: Int) {
        deleteItem(position)
    }
}