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

        workoutManagerViewModel.state.map { WorkoutManagerUiMapper.map(it) }.observe(this) {
            Log.d("WORKOUT UI", it.state.name)
            when (it.state) {
                WorkoutState.MINIMIZED -> {
                    setWorkoutVisibility(View.VISIBLE)
                    stopWorkoutFragment()
                }

                WorkoutState.ACTIVE -> {
                    setWorkoutVisibility(View.VISIBLE)
                    startWorkoutFragment()
                }

                WorkoutState.INACTIVE -> {
                    setWorkoutVisibility(View.GONE)
                    stopWorkoutFragment()
                }
            }
        }
    }

    private fun setWorkoutVisibility(visibility: Int) {
        binding.apply {
            timer.visibility = visibility
            workoutName.visibility = visibility
            shadow.visibility = visibility
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun startWorkoutFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val existingFragment = fragmentManager.findFragmentByTag(WORKOUT_FRAGMENT)
        if (existingFragment == null) {
            val fragment = WorkoutFragment()
            fragmentTransaction.add(R.id.container, fragment, WORKOUT_FRAGMENT)
        }

        fragmentTransaction.commit()
    }

    private fun stopWorkoutFragment() {
        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(WORKOUT_FRAGMENT)
        fragment?.let {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.remove(it)
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
