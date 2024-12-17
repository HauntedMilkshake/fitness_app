package bg.zahov.app.ui

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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bg.zahov.app.Routes
import bg.zahov.fitness.app.R

@Composable
fun BottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        BottomBarInfo(
            titleId = R.string.home,
            iconId = R.drawable.ic_bottom_nav_home,
            route = Routes.Home.route
        ),
        BottomBarInfo(
            titleId = R.string.history,
            iconId = R.drawable.ic_clock,
            route = Routes.History.route
        ),
        BottomBarInfo(
            titleId = R.string.workout,
            iconId = R.drawable.ic_plus,
            route = Routes.Workout.route
        ),
        BottomBarInfo(
            titleId = R.string.exercise,
            iconId = R.drawable.ic_exercise,
            route = Routes.Exercises.route
        ),
        BottomBarInfo(
            titleId = R.string.measure,
            iconId = R.drawable.ic_measures,
            route = Routes.Measure.route
        ),

        )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onBackground,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->
            NavigationBarItem(
                label = {
                    Text(text = stringResource(screen.titleId))
                },
                icon = {
                    Icon(painter = painterResource(screen.iconId), contentDescription = "")
                },
                selected = currentRoute == screen.route,
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
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.background,
                    selectedIconColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        }
    }
}

data class BottomBarInfo(
    val titleId: Int,
    val iconId: Int,
    val route: String,
)