package bg.zahov.app.ui.exercise.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import bg.zahov.app.data.model.BodyPart
import bg.zahov.app.data.model.Category
import bg.zahov.app.data.model.SelectableFilter
import bg.zahov.app.ui.exercise.ExerciseViewModel
import bg.zahov.app.util.SpacingItemDecoration
import bg.zahov.app.util.applyScaleAnimation
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.DialogFragmentFiltersBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class FilterDialog : DialogFragment() {
    private var _binding: DialogFragmentFiltersBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val filterViewModel: FilterViewModel by viewModels()

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
            val bodyPartAdapter = configureFilterRecyclerView(
                bodyPartRecyclerView,
                enumValues<BodyPart>().map { SelectableFilter(it.name) })
            filterViewModel.bodyPartFilters.observe(viewLifecycleOwner) {
                bodyPartAdapter.updateItems(it)
            }

            val categoryAdapter =configureFilterRecyclerView(
                categoryRecyclerView,
                enumValues<Category>().map { SelectableFilter(it.name) })
            filterViewModel.categoryFilters.observe(viewLifecycleOwner) {
                categoryAdapter.updateItems(it)
            }
        }
    }

    private fun configureFilterRecyclerView(
        recyclerView: RecyclerView,
        items: List<SelectableFilter>
    ): FilterAdapter {
        val filterAdapter = FilterAdapter(false).apply {
            itemClickListener = object : FilterAdapter.ItemClickListener<SelectableFilter> {
                override fun onItemClicked(item: SelectableFilter, clickedView: View) {
                    filterViewModel.onFilterClicked(item)
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
        return filterAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterExercisesSearch"
    }
}
