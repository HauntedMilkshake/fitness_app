package bg.zahov.app.ui.exercise.info

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
import androidx.viewpager2.adapter.FragmentStateAdapter
import bg.zahov.app.data.model.state.ExerciseNavigationUiMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.app.hideTopBar
import bg.zahov.app.ui.exercise.info.charts.ExerciseChartsFragment
import bg.zahov.app.ui.exercise.info.history.ExerciseHistoryFragment
import bg.zahov.app.ui.exercise.info.records.ExerciseRecordsFragments
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentExerciseInfoBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ExerciseNavigationFragment : Fragment() {
    private var _binding: FragmentExerciseInfoBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val exerciseNavigationViewModel: ExerciseNavigationViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExerciseInfoBinding.inflate(inflater, container, false)
        requireActivity().hideTopBar()
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbarExercise)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_exercise_information, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }

                    R.id.edit -> {
                        true
                    }

                    else -> false
                }
            }

        })
        binding.apply {
            exerciseNavigationViewModel.state.map { ExerciseNavigationUiMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    toolbarExercise.title = it.exerciseName
                    it.message?.let { message->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        viewPager.currentItem = it.position
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
            viewPager.apply {
                adapter = object : FragmentStateAdapter(this@ExerciseNavigationFragment) {
                    override fun getItemCount(): Int = ResourceStore.tabList.size

                    override fun createFragment(position: Int): Fragment =
                        ResourceStore.pagerFragments[position]
                }
            }

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = getString(ResourceStore.tabList[position])
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface ResourceStore {
    companion object {
        val tabList = listOf(R.string.history, R.string.Charts, R.string.records)
        val pagerFragments =
            listOf(ExerciseHistoryFragment(), ExerciseChartsFragment(), ExerciseRecordsFragments())
    }
}