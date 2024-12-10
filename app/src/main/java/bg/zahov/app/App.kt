package bg.zahov.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    val navController = rememberNavController()

    MainNavGraph(
        navController = navController
    )
}