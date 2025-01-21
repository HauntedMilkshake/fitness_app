package bg.zahov.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import bg.zahov.app.Inject.serviceErrorHandler
import bg.zahov.app.data.model.ServiceState
import bg.zahov.app.data.model.state.ShutDownData
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val workoutManagerViewModel: WorkoutManagerViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            loadingViewModel.loading.value
        }
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            launch {
                lifecycleScope.launch {
                    loadingViewModel.navigationTarget.collect {
                        navController.navigate(it)
                    }
                }
            }
            launch {
                serviceErrorHandler.observeServiceState()
                    .collect { serviceState ->
                        when (serviceState) {
                            ServiceState.Unavailable -> {}
                            ServiceState.Shutdown -> finish()
                            else -> ShutDownData()
                        }
                    }
            }
        }
        setContent {
//            App(workoutManagerViewModel)
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