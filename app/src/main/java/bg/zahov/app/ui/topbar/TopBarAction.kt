package bg.zahov.app.ui.topbar

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarAction(
    topBarState: TopBarState.TitleWithAction,
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
                    topBarState.backButtonIconId?.let { icon ->
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = topBarState.onActionClick) {
                Icon(
                    painter = painterResource(topBarState.actionButtonIconId),
                    contentDescription = null
                )
            }
        }
    )
}