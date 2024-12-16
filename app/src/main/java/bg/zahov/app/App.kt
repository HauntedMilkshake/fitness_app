package bg.zahov.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun App() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {}
    ) {padding->
        MainNavGraph(
            modifier = Modifier.padding(padding),
            navController = navController
        )
    }
}