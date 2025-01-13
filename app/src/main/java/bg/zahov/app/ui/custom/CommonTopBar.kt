package bg.zahov.app.ui.custom

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import bg.zahov.app.ui.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    topBarState: TopBarState,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(topBarState.titleId),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            topBarState.onBackClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        painter = painterResource(topBarState.backButtonIconId),
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            topBarState.onActionClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        painter = painterResource(topBarState.actionButtonIconId),
                        contentDescription = null
                    )
                }
            }
        }
    )
}