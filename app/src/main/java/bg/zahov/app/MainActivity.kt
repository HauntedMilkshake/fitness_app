package bg.zahov.app

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.map
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import bg.zahov.app.data.model.AuthUiModelMapper
import bg.zahov.app.data.model.WorkoutManagerUiMapper
import bg.zahov.app.data.model.WorkoutState
import bg.zahov.app.ui.workout.WorkoutFragment
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

        authViewModel.state.map { AuthUiModelMapper.map(it) }.observe(this) {
            if (it.isAuthenticated) navController.navigate(R.id.welcome_to_loading)
        }

        //?
        hideBottomNav()

        workoutManagerViewModel.state.map { WorkoutManagerUiMapper.map(it) }.observe(this) {
            when (it.state) {
                WorkoutState.MINIMIZED -> {
                    showBottomNav()
                    setWorkoutVisibility(View.VISIBLE)
                    stopWorkoutFragment()
                }

                WorkoutState.ACTIVE -> {
                    hideBottomNav()
                    setWorkoutVisibility(View.GONE)
                    startWorkoutFragment()
                }

                WorkoutState.INACTIVE -> {
                    showBottomNav()
                    setWorkoutVisibility(View.GONE)
                    stopWorkoutFragment()
                }
            }
        }

        workoutManagerViewModel.template.observe(this) {
            binding.workoutName.text = it.name
        }

        workoutManagerViewModel.timer.observe(this) {
            Log.d("TIMER ACTIVITY", it)
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

    private fun startWorkoutFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        if (supportFragmentManager.findFragmentByTag(WORKOUT_FRAGMENT) == null) {
            fragmentTransaction.add(R.id.nav_host_fragment, WorkoutFragment(), WORKOUT_FRAGMENT)
        }

        fragmentTransaction.commit()
    }

    private fun stopWorkoutFragment() {
        supportFragmentManager.findFragmentByTag(WORKOUT_FRAGMENT)?.let {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.remove(it)
            Log.d("REMOVED", "FRAGMETN")
            fragmentTransaction.commit()
        }
    }

    companion object {
        const val WORKOUT_FRAGMENT = "WorkoutFragment"
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
