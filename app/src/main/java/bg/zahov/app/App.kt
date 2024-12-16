package bg.zahov.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.rememberNavController
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    var topBar: TopBar? = null
    FitnessTheme {
        Scaffold(
            topBar = {
                topBar?.let { topbar ->
                    TopAppBar(
                        modifier = Modifier,
                        title = {
                            Text(
                                text = stringResource(topbar.titleId),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_settings),
                                    contentDescription = stringResource(R.string.menu)
                                )
                            }
                        }
                    )
                }
            }
        ) { padding ->
            MainNavGraph(
                modifier = Modifier.padding(padding),
                navController = navController,
                topBarCall = { topBar = it }
            )
        }
    }
}