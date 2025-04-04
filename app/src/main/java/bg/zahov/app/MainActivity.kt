package bg.zahov.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import bg.zahov.app.data.interfaces.ServiceErrorHandler
import bg.zahov.app.data.model.ServiceState
import bg.zahov.app.data.model.state.ShutDownData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val workoutManagerViewModel: WorkoutManagerViewModel by viewModels()
    private val loadingViewModel: LoadingViewModel by viewModels()

    @Inject lateinit var serviceError: ServiceErrorHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            loadingViewModel.loading.value
        }

        lifecycleScope.launch {
            serviceError.observeServiceState()
                .collect { serviceState ->
                    when (serviceState) {
                        ServiceState.Unavailable -> {}
                        ServiceState.Shutdown -> finish()
                        else -> ShutDownData()
                    }
                }
        }

        setContent {
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