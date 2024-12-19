package bg.zahov.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import bg.zahov.app.ui.BottomBar
import bg.zahov.app.ui.TopBar
import bg.zahov.app.ui.theme.FitnessTheme

@Composable
fun App() {
    val navController = rememberNavController()
    FitnessTheme {
        Scaffold(
            topBar = {
                TopBar(navController = navController)
            },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) { padding ->
            MainNavGraph(
                modifier = Modifier.padding(padding),
                navController = navController,
            )
        }
    }
}