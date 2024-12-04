package bg.zahov.app.ui.custom

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import bg.zahov.fitness.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopAppBar(
    title: Int,
    onNavigateHome: (() -> Unit)? = null,
    dropDownMenuItems: List<(@Composable (() -> Unit) -> Unit)>? = null,
    onDropDownMenuClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(text = stringResource(title), maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                    contentDescription = "Menu"
                )
            }
            dropDownMenuItems?.let { items ->
                DropdownMenu(
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
