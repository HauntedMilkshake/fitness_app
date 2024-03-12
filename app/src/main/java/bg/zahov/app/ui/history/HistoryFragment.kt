package bg.zahov.app.ui.history

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.Workout
import bg.zahov.app.data.model.state.HistoryUiMapper
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
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_up)
        exitTransition = inflater.inflateTransition(R.transition.fade_out)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            val historyAdapter = HistoryAdapter().apply {
                itemClickListener = object : HistoryAdapter.ItemClickListener<Workout> {
                    override fun onWorkoutClick(item: Workout, position: Int) {
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
                if (it.shutdown) findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}