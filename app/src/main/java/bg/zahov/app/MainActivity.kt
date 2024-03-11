package bg.zahov.app

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.map
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import bg.zahov.app.data.model.state.AuthUiModelMapper
import bg.zahov.app.data.model.state.WorkoutManagerUiMapper
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()
    private val workoutManagerViewModel: WorkoutManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)

        binding.bottomNavigation.setupWithNavController(navController)
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.title = "TEST"
        authViewModel.state.map { AuthUiModelMapper.map(it) }.observe(this) {
            if (it.isAuthenticated) navController.navigate(R.id.welcome_to_loading)
        }

        workoutManagerViewModel.state.map { WorkoutManagerUiMapper.map(it) }.observe(this) {
            setWorkoutVisibility(it.trailingWorkoutVisibility)
            if (it.openWorkout) navController.navigate(R.id.to_workout_fragment)
        }

        workoutManagerViewModel.template.observe(this) {
            binding.workoutName.text = it.name
        }

        workoutManagerViewModel.timer.observe(this) {
            binding.timer.text = it
        }

        binding.trailingWorkout.setOnClickListener {
            workoutManagerViewModel.updateStateToActive()
        }
    }

    private fun setWorkoutVisibility(visibility: Int) {
        binding.apply {
            trailingWorkout.visibility = visibility
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}

fun FragmentActivity.hideBottomNav() {
    findViewById<BottomNavigationView>(R.id.bottom_navigation)?.apply {
        visibility = View.GONE
    }
}

fun FragmentActivity.showBottomNav() {
    findViewById<BottomNavigationView>(R.id.bottom_navigation)?.apply {
        visibility = View.VISIBLE
    }
}
//
//fun FragmentActivity.setToolbarTitle(titleResource: Int) {
//    findViewById<MaterialTextView>(R.id.title)?.apply {
//        setTitle(titleResource)
//    }
//}
//
//fun FragmentActivity.setToolbarVisibility(toolBarVisibility: Int) {
//    findViewById<CoordinatorLayout>(R.id.coordinatorLayout)?.apply {
//        visibility = toolBarVisibility
//    }
//}
