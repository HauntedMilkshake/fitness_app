package bg.zahov.app

import bg.zahov.fitness.app.R

sealed class Routes(val route: String, val topBar: TopBar? = null, val bottomBar: Boolean = false) {
    data object Welcome : Routes("welcomeScreen")
    data object Signup : Routes("SignupScreen")
    data object Login : Routes("LoginScreen")
    data object Loading : Routes("LoadingScreen")
    data object Home : Routes("HomeScreen", topBar = TopBar(titleId = R.string.home), bottomBar = true)
    data object History : Routes("HistoryScreen", topBar = TopBar(titleId = R.string.history), bottomBar = true)
    data object Workout : Routes("WorkoutScreen", topBar = TopBar(titleId = R.string.exercise), bottomBar = true)
    data object Exercises : Routes("ExercisesScreen", topBar = TopBar(titleId = R.string.exercise), bottomBar = true)
    data object ExerciseInfo : Routes("ExerciseInfoScreen", topBar = TopBar(titleId = R.string.exercise))
    data object Measure : Routes("MeasureScreen", topBar = TopBar(titleId = R.string.measure), bottomBar = true)
    data object MeasureInfo : Routes("MeasureInfoScreen", topBar = TopBar(titleId = R.string.history_measurement))

}

data class TopBar(val titleId: Int)