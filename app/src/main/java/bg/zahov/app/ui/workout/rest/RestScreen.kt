package bg.zahov.app.ui.workout.rest

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R


@Composable
fun RestScreen(restViewModel: RestTimerViewModel = viewModel(), navigate: () -> Unit) {
    val state by restViewModel.uiState.collectAsStateWithLifecycle()
    val toast by ToastManager.messages.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(toast) {
        toast?.let { message ->
            Toast.makeText(context, context.getString(message.messageResId), Toast.LENGTH_SHORT)
                .show()
        }
    }
    when (state) {
        is Rest.Default -> {
            val cast = (state as Rest.Default)
            RestScreenContent(
                insideProgressContent = {
                    if (!cast.isCustomTimer) {
                        Column {
                            for (i in 0 until 4) {
                                val stringValue = (state as Rest.Default).rests[i]
                                TimerButton(
                                    text = stringValue,
                                    onClick = { restViewModel.onDefaultTimerClick(stringValue) })
                            }
                        }
                    } else {
                        ListPicker(
                            items = cast.rests,
                            selectedItem = cast.pickerValue,
                            onItemSelected = {
                                restViewModel.updateNumberPicker(it)
                            }
                        )
                    }
                },
                footer = {
                    TimerButton(
                        text = if (!cast.isCustomTimer) stringResource(R.string.create_custom_timer) else stringResource(
                            R.string.start_custom_rest
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        onClick = { if (!cast.isCustomTimer) restViewModel.onCreateCustomTimerClick() else restViewModel.onCustomTimerStart() })
                }
            )
        }

        is Rest.Resting -> {
            val cast = state as Rest.Resting
            RestScreenContent(timeProgress = cast.timer,
                insideProgressContent = {
                    TimerLabels(cast.startingTime)
                },
                footer = {
                    EditButtons(
                        timeDelta = cast.increment,
                        onAddTime = { restViewModel.addTime() },
                        onRemoveTime = { restViewModel.removeTime() },
                        onSkip = { restViewModel.cancelTimer() }
                    )
                })
        }

        is Rest.Finished -> {
            LaunchedEffect(Unit) {
                navigate()
            }
        }
    }
}

@Composable
fun RestScreenContent(
    timeProgress: Float? = null,
    insideProgressContent: @Composable (BoxScope.() -> Unit),
    footer: @Composable (ColumnScope.() -> Unit),
) {
    FitnessTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { timeProgress ?: 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                insideProgressContent()
            }

            footer()
        }
    }
}

@Composable
fun EditButtons(
    timeDelta: String,
    modifier: Modifier = Modifier,
    onAddTime: () -> Unit,
    onRemoveTime: () -> Unit,
    onSkip: () -> Unit,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        TimerButton(
            text = stringResource(R.string.add_to_rest_time, timeDelta),
            backgroundColor = MaterialTheme.colorScheme.background,
            textColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = onAddTime
        )

        Spacer(modifier.padding(4.dp))

        TimerButton(
            text = stringResource(R.string.remove_from_rest_time, timeDelta),
            backgroundColor = MaterialTheme.colorScheme.background,
            textColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = onRemoveTime
        )

        Spacer(modifier.padding(4.dp))

        TimerButton(
            text = stringResource(R.string.skip),
            backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            textColor = MaterialTheme.colorScheme.onBackground,
            onClick = onSkip
        )
    }
}

@Composable
fun TimerButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun TimerLabels(rest: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = rest, color = MaterialTheme.colorScheme.primary)
    }
}

/**
 * Converts a pixel value to density-independent pixels (dp) using the current screen density.
 *
 * @param pixel The pixel value to be converted into dp.
 * @return [androidx.compose.ui.unit.Dp]
 *
 * @see LocalDensity
 */
@Composable
fun getDp(pixel: Int) = with(LocalDensity.current) {
    pixel.toDp()
}

/**
 * A composable function that displays a scrollable list of items, allowing users to select an item
 * by centering it in a picker-like UI. It provides a visual indicator for the selected item
 * and updates the selection as the user scrolls through the list.
 *
 * @param T The type of items in the list.
 * @param items A list of items to display in the picker. Each item will be displayed as a text element.
 * @param selectedItem The currently selected item in the list. The list scrolls to this item initially.
 * @param onItemSelected A callback function invoked when a new item is selected (centered in the picker view).
 *
 * ### Behavior:
 * - The list initializes by centering on the `selectedItem`.
 * - As the user scrolls, once the scrolling stops, the centered item becomes the selected item.
 * - The picker applies a shadow and a background to visually highlight the selection area.
 *
 * ### Notes:
 * - `LazyColumn`'s initial position is set based on the index of `selectedItem`.
 * - The picker calculates the nearest item index when scrolling stops, based on scroll offset.
 *
 * @see LazyColumn
 * @see Box
 * @see onItemSelected
 */

//<T>
@Composable
fun <T> ListPicker(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit = {},
) {
    val selectedIndex = items.indexOf(selectedItem)
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    var boxHeightPx by rememberSaveable { mutableIntStateOf(0) }
    val itemHeightPx by remember { mutableIntStateOf(0) }


    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (!lazyListState.isScrollInProgress) {
            val centeredIndex =
                if (lazyListState.firstVisibleItemScrollOffset > itemHeightPx / 2) lazyListState.firstVisibleItemIndex + 1 else lazyListState.firstVisibleItemIndex
            if (items[centeredIndex] != selectedItem) {
                onItemSelected(items[centeredIndex])
            }
        }
    }

    Box(
        modifier = Modifier
            .sizeIn(maxWidth = 100.dp)
            .wrapContentHeight()
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.sizeIn(maxHeight = getDp(boxHeightPx * 3)),
            //this padding ensures there is enough space to hide elements
            contentPadding = PaddingValues(getDp(boxHeightPx / 2))
        ) {
            items(items) { item ->
                Text(
                    text = item.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged {
                            boxHeightPx = it.height
                        },
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        Column(
            modifier = Modifier
                .height(IntrinsicSize.Max)
//                .height(getDp(boxHeightPx))
                .align(Alignment.Center)
                .onSizeChanged {
                    boxHeightPx = it.height
                }
        ) {
            HorizontalDivider(thickness = 3.dp)
            //This text field is placed to help the box be the appropriate size for the text used in the lazy column
            Text(text = "", style = MaterialTheme.typography.bodyLarge)
            HorizontalDivider(thickness = 3.dp)

        }
    }
}