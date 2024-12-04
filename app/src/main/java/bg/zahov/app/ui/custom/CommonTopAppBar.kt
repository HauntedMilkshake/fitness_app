package bg.zahov.app.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    title: Int,
    modifier: Modifier = Modifier,
    onNavigateHome: (() -> Unit)? = null,
    dropDownMenuItems: List<(@Composable (() -> Unit) -> Unit)>? = null,
    onDropDownMenuClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(title),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            onNavigateHome?.let {
                IconButton(onClick = onNavigateHome) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back_arrow),
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = {
                onDropDownMenuClick?.let { it() } ?: run { showMenu = true }
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_dots),
                    contentDescription = stringResource(R.string.menu)
                )
            }
            dropDownMenuItems?.let { items ->
                DropdownMenu(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(8.dp)
                    ),
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    items.forEach { item ->
                        item { showMenu = false }
                    }
                }
            }
        }
    )
}