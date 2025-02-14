package bg.zahov.app.ui.topbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import bg.zahov.app.AddTemplateWorkout
import bg.zahov.app.Calendar
import bg.zahov.app.EditProfile
import bg.zahov.app.ExerciseAdd
import bg.zahov.app.Exercises
import bg.zahov.app.History
import bg.zahov.app.HistoryInfo
import bg.zahov.app.Home
import bg.zahov.app.Measure
import bg.zahov.app.MeasureInfo
import bg.zahov.app.Rest
import bg.zahov.app.Settings
import bg.zahov.app.StartWorkout
import bg.zahov.app.Workout
import bg.zahov.app.ui.exercise.topbar.TopBarExercise
import bg.zahov.app.ui.topbar.workout.TopBarWorkout
import bg.zahov.app.ui.history.info.topbar.HistoryInfoTopBar
import bg.zahov.fitness.app.R
import kotlin.reflect.KClass

sealed class TopBarState {
    data class Title(
        val titleId: Int,
        val modifier: Modifier = Modifier
    ) : TopBarState()

    data class TitleWithBack(
        val titleId: Int,
        val backButtonIconId: Int,
        val onBackClick: () -> Unit,
        val modifier: Modifier = Modifier
    ) : TopBarState()

    data class TitleWithAction(
        val titleId: Int,
        val actionButtonIconId: Int,
        val onActionClick: () -> Unit,
        val backButtonIconId: Int? = null,
        val onBackClick: (() -> Unit)? = null,
        val modifier: Modifier = Modifier
    ) : TopBarState()

    data object Exercise : TopBarState()

    data object HistoryInfo : TopBarState()

    data object Workout : TopBarState()
}

@Composable
fun topAppBarConfiguration(navController: NavController): Map<KClass<*>, TopBarState> {
    return mapOf(
        StartWorkout::class to TopBarState.Title(
            titleId = R.string.start_workout
        ),
        Home::class to TopBarState.TitleWithAction(
            titleId = R.string.home,
            actionButtonIconId = R.drawable.ic_settings,
            onActionClick = { navController.navigate(Settings) }
        ),
        Settings::class to TopBarState.TitleWithAction(
            titleId = R.string.settings_text,
            actionButtonIconId = R.drawable.ic_profile_circle,
            onActionClick = { navController.navigate(EditProfile) },
            backButtonIconId = R.drawable.ic_back_arrow,
            onBackClick = { navController.popBackStack() }
        ),
        EditProfile::class to TopBarState.TitleWithBack(
            titleId = R.string.edit_profile_text,
            backButtonIconId = R.drawable.ic_back_arrow,
            onBackClick = { navController.popBackStack() }
        ),
        History::class to TopBarState.TitleWithAction(
            titleId = R.string.history,
            actionButtonIconId = R.drawable.ic_calendar,
            onActionClick = { navController.navigate(Calendar) }
        ),
        HistoryInfo::class to TopBarState.HistoryInfo,
        Calendar::class to TopBarState.TitleWithBack(
            titleId = R.string.history,
            backButtonIconId = R.drawable.ic_back_arrow,
            onBackClick = { navController.popBackStack() }
        ),
        Workout::class to TopBarState.Title(
            titleId = R.string.workout
        ),
        Measure::class to TopBarState.Title(
            titleId = R.string.measure
        ),
        MeasureInfo::class to TopBarState.TitleWithBack(
            titleId = R.string.history_measurement,
            backButtonIconId = R.drawable.ic_back_arrow,
            onBackClick = { navController.popBackStack() }
        ),
        Exercises::class to TopBarState.Exercise,
        ExerciseAdd::class to TopBarState.TitleWithBack(
            titleId = R.string.add_exercise,
            backButtonIconId = R.drawable.ic_back_arrow,
            onBackClick = { navController.popBackStack() }
        ),
        AddTemplateWorkout::class to TopBarState.TitleWithAction(
            titleId = R.string.new_workout_template,
            actionButtonIconId = R.drawable.ic_plus,
            onActionClick = { /* TODO() */ },
            onBackClick = { navController.navigateUp() }
        ),

        Workout::class to TopBarState.Workout,

        Rest::class to TopBarState.TitleWithBack(
            titleId = R.string.rest_top_bar_title,
            backButtonIconId = R.drawable.ic_close,
            onBackClick = { navController.popBackStack() }
        )
    )
}

@Composable
fun TopBar(modifier: Modifier = Modifier, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topBarState = topAppBarConfiguration(navController).entries.find {
        currentDestination?.hasRoute(it.key) == true
    }?.value

    topBarState?.let { state ->
        when (state) {
            is TopBarState.Title -> {
                TopBarTitle(
                    topBarState = state,
                    modifier = modifier
                )
            }

            is TopBarState.TitleWithBack -> {
                TopBarBack(
                    topBarState = state,
                    modifier = modifier
                )
            }

            is TopBarState.TitleWithAction -> {
                TopBarAction(
                    topBarState = state,
                    modifier = modifier
                )
            }

            TopBarState.Exercise -> {
                TopBarExercise(onAddClick = { navController.navigate(ExerciseAdd) })
            }

            TopBarState.HistoryInfo -> {
                HistoryInfoTopBar(
                    onBack = { navController.navigateUp() }
                )
            }

            is TopBarState.Workout -> {
                TopBarWorkout(
                    onRestClick = { navController.navigate(Rest) },
                )
            }
        }
    }
}