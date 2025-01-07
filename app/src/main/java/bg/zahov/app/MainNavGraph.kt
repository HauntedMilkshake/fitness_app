package bg.zahov.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
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
import bg.zahov.app.ui.settings.SettingsScreen
import bg.zahov.app.ui.welcome.WelcomeScreen
import bg.zahov.app.ui.workout.WorkoutScreen

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Loading
    ) {
        composable<Welcome> {
            WelcomeScreen(
                onSignup = {
                    navController.navigate(Signup) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogin = {
                    navController.navigate(Login) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Signup> {
            SignupScreen(
                onAuthenticate = { navController.navigate(Loading) },
                onNavigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Login> {
            LoginScreen(
                onAuthenticate = { navController.navigate(Loading) },
                onNavigateToSignUp = {
                    navController.navigate(Signup) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Loading> {
            LoadingScreen(
                navigateWelcome = { navController.navigate(Welcome) },
                navigateHome = { navController.navigate(Home) }
            )
        }
        composable<Home> {
            HomeScreen()
        }

        composable<Workout> {
            WorkoutScreen(
                onCancel = {},
                onAddExercise = {},
                onBackPressed = {},
                onReplaceExercise = {}
            )
        }

        composable<Exercises> {
            ExercisesScreen(
                navigateInfo = { navController.navigate(ExerciseInfo) },
                navigateBack = { navController.navigateUp() })
        }

        composable<ExerciseInfo> {
            ExerciseInfoScreen()
        }
        composable<History> {
            HistoryScreen(onItemClick = {})
        }
        composable<Measure> {
            MeasuresScreen(
                navigateInfo = { navController.navigate(MeasureInfo) }
            )
        }
        composable<MeasureInfo> {
            MeasurementInfoScreen()
        }
        composable<Settings> {
            SettingsScreen(
                navigateBack = { navController.navigateUp() },
                navigateEditProfile = { navController.navigate(EditProfile) }
            )
        }
    }
}