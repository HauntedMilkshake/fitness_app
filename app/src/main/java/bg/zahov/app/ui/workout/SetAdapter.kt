package bg.zahov.app.ui.workout

import android.view.View
import bg.zahov.app.data.model.Category
import bg.zahov.app.util.BaseAdapter
import bg.zahov.app.util.SwipeGesture
import bg.zahov.app.data.model.ClickableSet
import bg.zahov.fitness.app.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class SetAdapter (private val category: Category) : BaseAdapter<ClickableSet>(
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
        private val firstInput = view.findViewById<MaterialTextView>(R.id.first_input_field)
        private val secondInput = view.findViewById<MaterialTextView>(R.id.second_input_field)
        private val check = view.findViewById<ShapeableImageView>(R.id.check)

        override fun bind(item: ClickableSet) {
            setIndicator.text = "$itemCount"
             previous.text = "" //TODO()
            setIndicator.setOnClickListener {
                itemClickListener?.onSetNumberClicked(item, itemView)
                //TODO(Add animation where it drops down and you can change the set type)
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
        fun onSwipe(position: Int)
    }


    override fun onSwipe(position: Int) {
        itemClickListener?.onSwipe(position)
    }
}