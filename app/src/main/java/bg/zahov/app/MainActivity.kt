package bg.zahov.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import bg.zahov.app.Inject.serviceErrorHandler
import bg.zahov.app.data.model.ServiceState
import bg.zahov.app.data.model.state.ShutDownData
import bg.zahov.fitness.app.R
import bg.zahov.fitness.app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val workoutManagerViewModel: WorkoutManagerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            serviceErrorHandler.observeServiceState()
                .collect { serviceState ->
                    when (serviceState) {
                        ServiceState.Unavailable -> {}
                        ServiceState.Shutdown -> finish()
                        else -> ShutDownData()
                    }
                }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val composeView = findViewById<ComposeView>(R.id.parentCompose)

        composeView.setContent {
            App(workoutManagerViewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        workoutManagerViewModel.saveWorkoutState()
    }

    override fun onStop() {
        super.onStop()
        workoutManagerViewModel.saveWorkoutState()
    }
}
