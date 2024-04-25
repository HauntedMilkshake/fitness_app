package bg.zahov.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.HistoryUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showBottomNav
import bg.zahov.app.showTopBar
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding
        get() = requireNotNull(_binding!!)

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        requireActivity().showTopBar()
        requireActivity().showBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setToolBarTitle(R.string.history)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_history, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.calendar -> {
                        findNavController().navigate(R.id.history_to_calendar)
                        true
                    }

                    else -> false
                }
            }
        })
        binding.apply {
            val historyAdapter = HistoryAdapter().apply {
                itemClickListener = object : HistoryAdapter.ItemClickListener<Workout> {
                    override fun onWorkoutClick(item: Workout, position: Int) {
                        findNavController().navigate(
                            R.id.history_to_history_info,
                            bundleOf(workoutId to item.id)
                        )
                    }
                }
            }

            workouts.apply {
                adapter = historyAdapter
                layoutManager = LinearLayoutManager(context)
            }

            historyViewModel.state.map { HistoryUiMapper.map(it) }.observe(viewLifecycleOwner) {
                circularProgressIndicator.visibility = it.loadingVisibility
                workouts.visibility = it.workoutVisibility
                historyAdapter.updateItems(it.workouts)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().showTopBar()
        requireActivity().showBottomNav()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val workoutId = "WORKOUT_ID"
    }
}