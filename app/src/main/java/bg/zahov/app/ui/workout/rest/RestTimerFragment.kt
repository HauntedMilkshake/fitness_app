package bg.zahov.app.ui.workout.rest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import bg.zahov.app.data.model.state.RestTimerUiModelMapper
import bg.zahov.app.hideBottomNav
import bg.zahov.app.setToolBarTitle
import bg.zahov.app.showTopBar
import bg.zahov.app.util.parseTimeStringToLong
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.FragmentRestTimerBinding

class RestTimerFragment : Fragment() {
    private var _binding: FragmentRestTimerBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    private val restTimerViewModel: RestTimerViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRestTimerBinding.inflate(inflater, container, false)
        requireActivity().showTopBar()
        requireActivity().hideBottomNav()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().showTopBar()
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        requireActivity().setToolBarTitle(R.string.rest_timer_text)
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_toolbar_rest_timer, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.rest_timer_to_workout)
                    true
                }

                else -> false
            }
        })
        binding.apply {
            restTimerViewModel.state.map { RestTimerUiModelMapper.map(it) }
                .observe(viewLifecycleOwner) {
                    customRestTimerPicker.visibility =
                        if (it.isAddingCustomTimer) View.VISIBLE else View.GONE
                    restButtons.visibility =
                        if (it.isAddingCustomTimer || it.isCountdown) View.GONE else View.VISIBLE
                    createCustomTimer.visibility =
                        if (it.isCountdown) View.GONE else View.VISIBLE

                    createCustomTimer.setText(if (it.isAddingCustomTimer) R.string.start_custom_rest else R.string.create_custom_timer)
                    if (it.isAddingCustomTimer) {
                        createCustomTimer.setOnClickListener {
                            restTimerViewModel.onCustomTimerStart(restTimerViewModel.getRestsArray()[customRestTimerPicker.value])
                        }
                    }

                    setTimerVisibility(if (it.isCountdown) View.VISIBLE else View.GONE)

                    currentTimer.text = it.time

                    if (it.finished) findNavController().navigate(R.id.rest_timer_to_workout)

                    if (it.isCountdown && it.time.isNotEmpty()) progressBar.setProgressWithAnimation(
                        it.time.parseTimeStringToLong().toFloat(), 1000
                    )
                }

            customRestTimerPicker.apply {
                minValue = 0
                maxValue = restTimerViewModel.getRestsArray().size - 1
                displayedValues = restTimerViewModel.getRestsArray().toTypedArray()
                setFormatter { p0 -> restTimerViewModel.getRestsArray()[p0] }
                textColor = resources.getColor(R.color.white, null)

            }

            restTimerViewModel.startingTime.observe(viewLifecycleOwner) {
                setTimer.text = it
                progressBar.apply {
                    if (it.isNotEmpty()) progressMax = it.parseTimeStringToLong().toFloat()
                }
            }

            restTimerViewModel.increment.observe(viewLifecycleOwner) {
                addTime.text = "+ $it"
                removeTime.text = "- $it"
            }

            createCustomTimer.setOnClickListener {
                restTimerViewModel.onCreateCustomTimer()
            }
            addTime.setOnClickListener {
                restTimerViewModel.addTime()
            }
            removeTime.setOnClickListener {
                restTimerViewModel.removeTime()
            }
            firstRestButton.setOnClickListener {
                restTimerViewModel.onDefaultTimerClick("1:00")
            }
            secondRestButton.setOnClickListener {
                restTimerViewModel.onDefaultTimerClick("1:30")
            }
            thirdRestButton.setOnClickListener {
                restTimerViewModel.onDefaultTimerClick("2:00")
            }
            fourthRestButton.setOnClickListener {
                restTimerViewModel.onDefaultTimerClick("2:30")
            }
            skip.setOnClickListener {
                restTimerViewModel.cancelTimer()
                findNavController().navigateUp()
            }
        }
    }

    private fun setTimerVisibility(visibility: Int) {
        binding.apply {
            addTime.visibility = visibility
            removeTime.visibility = visibility
            skip.visibility = visibility
            restInfoText.visibility = visibility
            currentTimer.visibility = visibility
            setTimer.visibility = visibility
        }
    }

    override fun onResume() {
        super.onResume()
        //damn we do be needing some other way to handle this :/
        requireActivity().hideBottomNav()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}