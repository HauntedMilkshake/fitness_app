package bg.zahov.app.ui.history.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bg.zahov.app.data.model.state.HistoryInfoUiMapper
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.ui.history.HistoryFragment.Companion.workoutId
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentHistoryInfoBinding

class HistoryInfoFragment : Fragment() {
    private var _binding: FragmentHistoryInfoBinding? = null
    private val binding
        get() = requireNotNull(_binding!!)

    private val historyInfoViewModel: HistoryInfoViewModel by viewModels()
    private val id by lazy {
        arguments?.getString(workoutId) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryInfoBinding.inflate(inflater, container, false)
        historyInfoViewModel.queryWorkout(id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_history_info, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().navigate(R.id.history_info_to_history)
                        true
                    }

                    R.id.delete -> {
                        historyInfoViewModel.delete()
                        findNavController().navigate(R.id.history_info_to_history)
                        true
                    }

                    R.id.save_as_workout_template -> {
                        historyInfoViewModel.saveAsTemplate()
                        true
                    }

                    else -> false
                }
            }
        })
        binding.apply {
            val historyInfoAdapter = HistoryInfoAdapter()
            historyInfoViewModel.state.map { HistoryInfoUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    it.workout?.let { data ->
                        requireActivity().setToolBarTitle(data.workoutName)
                        lastPerformed.text = data.workoutDate
                        duration.text = data.duration
                        volume.text = data.volume
                        prCount.text = data.prs
                        historyInfoAdapter.updateItems(data.adapterData)
                    }
                    showToast(it.message)
                    if (it.shutdown) {
                    }//TODO()
                }
            exercisesRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = historyInfoAdapter
            }
            performAgain.setOnClickListener {
                historyInfoViewModel.performAgain()
            }
        }
    }

    private fun showToast(message: String?) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}