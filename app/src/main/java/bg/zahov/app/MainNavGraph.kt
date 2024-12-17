package bg.zahov.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import bg.zahov.app.ui.authentication.login.LoginScreen
import bg.zahov.app.ui.authentication.signup.SignupScreen
import bg.zahov.app.ui.exercise.ExercisesScreen
import bg.zahov.app.ui.exercise.info.ExerciseInfoScreen
import bg.zahov.app.ui.history.HistoryScreen
import bg.zahov.app.ui.home.HomeScreen
import bg.zahov.app.ui.loading.LoadingScreen
import bg.zahov.app.ui.measures.MeasuresScreen
import bg.zahov.app.ui.measures.info.MeasurementInfoScreen
import bg.zahov.app.ui.welcome.WelcomeScreen
import bg.zahov.app.ui.workout.WorkoutScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    topBarCall: (TopBar?, Boolean) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Routes.Welcome.route
    ) {
        composable(Routes.Welcome.route) {
            topBarCall(Routes.Welcome.topBar, Routes.Welcome.bottomBar)
            WelcomeScreen(
                onSignup = { navController.navigate(Routes.Signup.route) },
                onLogin = { navController.navigate(Routes.Login.route) }
            )
        }
        composable(Routes.Signup.route) {
            topBarCall(Routes.Signup.topBar, Routes.Signup.bottomBar)
            SignupScreen(
                onAuthenticate = { navController.navigate(Routes.Loading.route) },
                onNavigateToLogin = { navController.navigate(Routes.Login.route) }
            )
        }
        composable(Routes.Login.route) {
            topBarCall(Routes.Login.topBar, Routes.Login.bottomBar)
            LoginScreen(
                onAuthenticate = { navController.navigate(Routes.Loading.route) },
                onNavigateToSignUp = { navController.navigate(Routes.Signup.route) }
            )
        }
        composable(Routes.Loading.route) {
            topBarCall(Routes.Loading.topBar, Routes.Loading.bottomBar)
            LoadingScreen(
                navigateWelcome = { navController.navigate(Routes.Welcome.route) },
                navigateHome = { navController.navigate(Routes.Home.route) }
            )
        }
        composable(Routes.Home.route) {
            topBarCall(Routes.Home.topBar, Routes.Home.bottomBar)
            HomeScreen()
        }

        composable(Routes.Workout.route) {
            topBarCall(Routes.Home.topBar, Routes.Home.bottomBar)
            WorkoutScreen(
                onCancel = {},
                onAddExercise = {},
                onBackPressed = {},
                onReplaceExercise = {}
            )
        }

        composable(Routes.Exercises.route) {
            topBarCall(Routes.Exercises.topBar, Routes.Exercises.bottomBar)
            ExercisesScreen(
                navigateInfo = { navController.navigate(Routes.ExerciseInfo.route) },
                navigateBack = { navController.navigateUp() })
        }

        composable(Routes.ExerciseInfo.route) {
            topBarCall(Routes.ExerciseInfo.topBar, Routes.ExerciseInfo.bottomBar)
            ExerciseInfoScreen()
        }
        composable(Routes.History.route) {
            topBarCall(Routes.History.topBar, Routes.History.bottomBar)
            HistoryScreen(onItemClick = {})
        }
        composable(Routes.Measure.route) {
            topBarCall(Routes.Measure.topBar, Routes.Measure.bottomBar)
            MeasuresScreen(
                navigateInfo = { navController.navigate(Routes.MeasureInfo.route) }
            )
        }
        composable(Routes.MeasureInfo.route) {
            topBarCall(Routes.MeasureInfo.topBar, Routes.MeasureInfo.bottomBar)
            MeasurementInfoScreen()
        }
    }
}