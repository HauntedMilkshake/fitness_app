package bg.zahov.app.ui

import android.annotation.SuppressLint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bg.zahov.app.Exercises
import bg.zahov.app.History
import bg.zahov.app.Home
import bg.zahov.app.Measure
import bg.zahov.app.Workout
import bg.zahov.fitness.app.R

@SuppressLint("RestrictedApi")
@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        BottomBarInfo(
            titleId = R.string.home,
            iconId = R.drawable.ic_bottom_nav_home,
            route = Home
        ),
        BottomBarInfo(
            titleId = R.string.history,
            iconId = R.drawable.ic_clock,
            route = History
        ),
        BottomBarInfo(
            titleId = R.string.workout,
            iconId = R.drawable.ic_plus,
            route = Workout
        ),
        BottomBarInfo(
            titleId = R.string.exercise,
            iconId = R.drawable.ic_exercise,
            route = Exercises
        ),
        BottomBarInfo(
            titleId = R.string.measure,
            iconId = R.drawable.ic_measures,
            route = Measure
        ),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    if (screens.any {screen->
            currentDestination?.hierarchy?.any {
                it.hasRoute(route = screen.route::class)
            } == true
        })
        NavigationBar(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            screens.forEach { screen ->
                NavigationBarItem(
                    label = {
                        Text(text = stringResource(screen.titleId))
                    },
                    icon = {
                        Icon(painter = painterResource(screen.iconId), contentDescription = null)
                    },
                    selected = currentDestination?.hierarchy?.any {
                        it.hasRoute(route = screen.route::class)
                    } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                        selectedTextColor = MaterialTheme.colorScheme.onBackground,
                        selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                        indicatorColor = MaterialTheme.colorScheme.secondary
                    ),
                )
            }
        }
}

data class BottomBarInfo<T : Any>(val titleId: Int, val iconId: Int, val route: T)





