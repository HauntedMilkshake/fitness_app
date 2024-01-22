package bg.zahov.app.ui.exercise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.Filter
import bg.zahov.app.util.SpacingItemDecoration
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.DialogFragmentFiltersBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

//FIXME binding should be disposed in onDestroyView
class FilterDialog : DialogFragment() {
    private var _binding: DialogFragmentFiltersBinding? = null
    private val binding get() = _binding!!
    private val exerciseViewModel: ExerciseViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogFragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            back.setOnClickListener {
                it.applyScaleAnimation()
                dismiss()
            }

            configureFilterRecyclerView(bodyPartRecyclerView, exerciseViewModel.getBodyPartItems())
            configureFilterRecyclerView(categoryRecyclerView, exerciseViewModel.getCategoryItems())
        }
    }

    private fun configureFilterRecyclerView(recyclerView: RecyclerView, items: List<Filter>) {
        val filterAdapter = FilterAdapter(false).apply {
            itemClickListener = object : FilterAdapter.ItemClickListener<Filter> {
                override fun onItemClicked(item: Filter, clickedView: View) {
//                    if (item.selected) exerciseViewModel.addFilter(item) else exerciseViewModel.removeFilter(
//                        item
//                    )
                }
            }
            updateItems(items)
        }

        recyclerView.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = filterAdapter
            addItemDecoration(
                SpacingItemDecoration(
                    resources.getDimensionPixelSize(R.dimen.item_spacing_right),
                    resources.getDimensionPixelSize(R.dimen.item_spacing_right),
                    resources.getDimensionPixelSize(R.dimen.item_spacing_bottom),
                    resources.getDimensionPixelSize(R.dimen.item_spacing_bottom)
                )
            )
        }
    }

    companion object {
        const val TAG = "FilterExercisesSearch"
    }
}
