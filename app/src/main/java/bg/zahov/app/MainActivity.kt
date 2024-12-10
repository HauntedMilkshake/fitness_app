package bg.zahov.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import bg.zahov.app.Inject.serviceErrorHandler
import bg.zahov.app.data.model.ServiceState
import bg.zahov.app.data.model.state.ShutDownData
import bg.zahov.app.data.model.state.WorkoutManagerUiMapper
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val workoutManagerViewModel: WorkoutManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val composeView = findViewById<ComposeView>(R.id.parentCompose)

        composeView.setContent {
            App()
        }
    }
}
//        val navController = findNavController(R.id.nav_host_fragment)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//
//        binding.bottomNavigation.setupWithNavController(navController)
//        setSupportActionBar(binding.toolbar)
//
//        workoutManagerViewModel.state.map { WorkoutManagerUiMapper.map(it) }.observe(this) {
//            setWorkoutVisibility(it.trailingWorkoutVisibility)
//            if (it.openWorkout) navController.navigate(R.id.to_workout_fragment)
//        }
//        lifecycleScope.launch {
//            serviceErrorHandler.observeServiceState()
//                .collect { serviceState ->
//                    when (serviceState) {
//                        ServiceState.Unavailable -> navController.navigate(R.id.to_shutting_down_fragment)
//                        ServiceState.Shutdown -> finish()
//                        else -> ShutDownData()
//                    }
//                }
//        }
//        workoutManagerViewModel.template.observe(this) {
//            binding.workoutName.text = it.name
//        }
//
//        workoutManagerViewModel.timer.observe(this) {
//            binding.timer.text = it
//        }
//
//        binding.trailingWorkout.setOnClickListener {
//            workoutManagerViewModel.updateStateToActive()
//        }

//
//    private fun setWorkoutVisibility(visibility: Int) {
//        binding.apply {
//            trailingWorkout.visibility = visibility
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        workoutManagerViewModel.saveWorkoutState()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        workoutManagerViewModel.saveWorkoutState()
//    }
//}
//
//fun FragmentActivity.hideBottomNav() {
//    findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
//
//}
//
//fun FragmentActivity.showBottomNav() {
//    findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
//}
//
//fun FragmentActivity.showTopBar() {
//    findViewById<AppBarLayout>(R.id.top_bar)?.visibility = View.VISIBLE
//    findViewById<MaterialToolbar>(R.id.toolbar)?.visibility = View.VISIBLE
//}
//
//fun FragmentActivity.hideTopBar() {
//    findViewById<AppBarLayout>(R.id.top_bar)?.visibility = View.GONE
//    findViewById<MaterialToolbar>(R.id.toolbar)?.visibility = View.GONE
//
//}
//
//fun FragmentActivity.setToolBarTitle(title: String) {
//    findViewById<MaterialToolbar>(R.id.toolbar)?.title = title
//}
//
//fun FragmentActivity.setToolBarTitle(title: Int) {
//    findViewById<MaterialToolbar>(R.id.toolbar)?.setTitle(title)
//}
