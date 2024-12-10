//package bg.zahov.app.ui.history.info
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.core.view.MenuProvider
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.navigation.fragment.findNavController
//import bg.zahov.app.hideBottomNav
//import bg.zahov.app.setToolBarTitle
//import bg.zahov.app.showBottomNav
//import bg.zahov.fitness.app.R
//import bg.zahov.fitness.app.databinding.FragmentHistoryInfoBinding
//import kotlinx.coroutines.launch
//
//class HistoryInfoFragment : Fragment() {
//
//    private var _binding: FragmentHistoryInfoBinding? = null
//    private val binding
//        get() = requireNotNull(_binding)
//
//    private val historyInfoViewModel: HistoryInfoViewModel by viewModels(
//        ownerProducer = { requireParentFragment() }
//    )
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
//    ): View {
//        activity?.hideBottomNav()
//        setupTopBar()
//        _binding = FragmentHistoryInfoBinding.inflate(inflater, container, false)
//
//        binding.composeScreen.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//        binding.composeScreen.setContent {
//            HistoryInfoScreen(
//                historyInfoViewModel = historyInfoViewModel,
//                onDelete = { findNavController().navigateUp() })
//        }
//        return binding.root
//    }
//
//    private fun setupTopBar() {
//        (activity as? AppCompatActivity)?.supportActionBar?.apply {
//            setDisplayHomeAsUpEnabled(true)
//            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
//        }
//
//        activity?.addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menu.clear()
//                menuInflater.inflate(R.menu.menu_toolbar_history_info, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                return when (menuItem.itemId) {
//                    R.id.home -> {
//                        findNavController().navigateUp()
//                        true
//                    }
//
//                    R.id.delete -> {
//                        historyInfoViewModel.delete()
//                        true
//                    }
//
//                    R.id.save_as_workout_template -> {
//                        historyInfoViewModel.saveAsTemplate()
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//        })
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
//                historyInfoViewModel.uiState.collect {
//                    activity?.setToolBarTitle(it.workoutName)
//                }
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        activity?.showBottomNav()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}