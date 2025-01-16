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
import bg.zahov.app.ui.exercise.add.AddExerciseScreen
import bg.zahov.app.ui.exercise.info.ExerciseInfoScreen
import bg.zahov.app.ui.history.HistoryScreen
import bg.zahov.app.ui.history.calendar.CalendarScreen
import bg.zahov.app.ui.history.info.HistoryInfoScreen
import bg.zahov.app.ui.home.HomeScreen
import bg.zahov.app.ui.loading.LoadingScreen
import bg.zahov.app.ui.measures.MeasuresScreen
import bg.zahov.app.ui.measures.info.MeasurementInfoScreen
import bg.zahov.app.ui.settings.SettingsScreen
import bg.zahov.app.ui.settings.profile.EditProfileScreen
import bg.zahov.app.ui.welcome.WelcomeScreen
import bg.zahov.app.ui.workout.AddTemplateWorkoutScreen
import bg.zahov.app.ui.workout.WorkoutScreen
import bg.zahov.app.ui.workout.start.StartWorkoutScreen
import bg.zahov.app.data.local.Settings

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Destinations.Loading
    ) {
        composable<Destinations.Welcome> {
            WelcomeScreen(
                onSignup = {
                    navController.navigate(Destinations.Signup) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogin = {
                    navController.navigate(Destinations.Login) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Destinations.Signup> {
            SignupScreen(
                onAuthenticate = { navController.navigate(Destinations.Loading) },
                onNavigateToLogin = {
                    navController.navigate(Destinations.Login) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Destinations.Login> {
            LoginScreen(
                onAuthenticate = {
                    navController.navigate(Destinations.Loading) {
                        popUpTo<Destinations.Login> { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Destinations.Signup) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable<Destinations.Loading> {
            LoadingScreen(
                navigateWelcome = {
                    navController.navigate(route = Destinations.Welcome) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateHome = {
                    navController.navigate(route = Destinations.Home) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable<Destinations.Home> {
            HomeScreen()
        }
        composable<Settings> {
            SettingsScreen(
                navigateBack = { navController.popBackStack() },
                navigateEditProfile = { navController.navigate(Destinations.EditProfile) }
            )
        }
        composable<Destinations.EditProfile> {
            EditProfileScreen()
        }

        composable<Destinations.Workout> {
            WorkoutScreen(
                onCancel = { navController.navigateUp() },
                onAddExercise = { navController.navigate(Destinations.Exercises(ExerciseArgs.ADD_EXERCISE_ARG)) },
                onBackPressed = { navController.navigateUp() },
                onReplaceExercise = { navController.navigate(Destinations.Exercises(ExerciseArgs.REPLACE_EXERCISE_ARG)) }
            )
        }
        composable<Destinations.Exercises> {
            ExercisesScreen(
                navigateInfo = { navController.navigate(Destinations.ExerciseInfo) },
                navigateBack = { navController.navigateUp() })
        }
        composable<Destinations.ExerciseAdd> {
            AddExerciseScreen(
                navigate = { navController.popBackStack() }
            )
        }
        composable<Destinations.ExerciseInfo> {
            ExerciseInfoScreen()
        }
        composable<Destinations.History> {
            HistoryScreen(onItemClick = { navController.navigate(Destinations.HistoryInfo) })
        }
        composable<Destinations.HistoryInfo> {
            HistoryInfoScreen(onDelete = { navController.navigateUp() })
        }
        composable<Destinations.Measure> {
            MeasuresScreen(
                navigateInfo = { navController.navigate(Destinations.MeasureInfo) }
            )
        }

        composable<Destinations.MeasureInfo> {
            MeasurementInfoScreen()
        }

        composable<Destinations.StartWorkout> {
            StartWorkoutScreen(
                onEditWorkout = { navController.navigate(Destinations.AddTemplateWorkout(it)) },
                onAddTemplateWorkout = { navController.navigate(Destinations.AddTemplateWorkout()) }
            )
        }
        composable<Destinations.AddTemplateWorkout> {
            AddTemplateWorkoutScreen(
                onAddExercise = { navController.navigate(Destinations.Exercises(ExerciseArgs.ADD_EXERCISE_ARG)) },
                onReplaceExercise = { navController.navigate(Destinations.Exercises(ExerciseArgs.REPLACE_EXERCISE_ARG)) },
                onBackPressed = { navController.navigateUp() },
                onCancel = { navController.navigateUp() }
            )
        }
        composable<Destinations.Calendar> {
            CalendarScreen()
        }
    }
}

object ExerciseArgs {
    const val REPLACE_EXERCISE_ARG = "REPLACING"
    const val ADD_EXERCISE_ARG = "SELECTING"
    const val WORKOUT_ID_TO_EDIT = "WORKOUTID"
    const val SELECT_EXERCISE_ARG = "ADDING"
    const val STATE_ARG = "State"
}