package bg.zahov.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import bg.zahov.app.AddExercise
import bg.zahov.app.EditProfile
import bg.zahov.app.Exercises
import bg.zahov.app.History
import bg.zahov.app.Home
import bg.zahov.app.Measure
import bg.zahov.app.MeasureInfo
import bg.zahov.app.Settings
import bg.zahov.app.Workout
import bg.zahov.app.ui.custom.CommonTopBar
import bg.zahov.app.ui.exercise.topbar.TopBarExercise
import bg.zahov.fitness.app.R

@Composable
fun TopBar(modifier: Modifier = Modifier, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topBarState = when {
        currentDestination?.hasRoute(Home::class) == true -> TopBarState(
            titleId = R.string.home,
            onActionClick = { navController.navigate(Settings) }
        )

        currentDestination?.hasRoute(Settings::class) == true -> TopBarState(
            titleId = R.string.settings_text,
            actionButtonIconId = R.drawable.ic_profile_circle,
            onActionClick = { navController.navigate(EditProfile) },
            onBackClick = { navController.popBackStack() }
        )

        currentDestination?.hasRoute(EditProfile::class) == true -> TopBarState(
            titleId = R.string.edit_profile_text,
            onBackClick = { navController.popBackStack() }
        )

        currentDestination?.hasRoute(History::class) == true -> TopBarState(
            titleId = R.string.history
        )

        currentDestination?.hasRoute(Workout::class) == true -> TopBarState(
            titleId = R.string.workout
        )

        currentDestination?.hasRoute(Measure::class) == true -> TopBarState(
            titleId = R.string.measure,
            actionButtonIconId = R.drawable.ic_plus,
            onActionClick = {}
        )

        currentDestination?.hasRoute(MeasureInfo::class) == true -> TopBarState(
            titleId = R.string.history_measurement,
            onBackClick = { navController.popBackStack() }
        )

        currentDestination?.hasRoute(AddExercise::class)== true->TopBarState(
            titleId = R.string.add_exercise,
            onBackClick = { navController.popBackStack() }
        )

        else -> null
    }
    topBarState?.let {
        CommonTopBar(topBarState = it, modifier = modifier)
    }
    if (currentDestination?.hasRoute(Exercises::class) == true) {
        TopBarExercise({ navController.navigate(AddExercise) })
    }
}

data class TopBarState(
    val titleId: Int,
    val actionButtonIconId: Int = R.drawable.ic_settings,
    val backButtonIconId: Int = R.drawable.ic_back_arrow,
    val onActionClick: (() -> Unit)? = null,
    val onBackClick: (() -> Unit)? = null
)