package bg.zahov.app.ui.workout.rest

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.ToastManager
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R
import com.chargemap.compose.numberpicker.ListItemPicker

@Composable
fun RestScreen(restViewModel: RestTimerViewModel = hiltViewModel(), navigate: () -> Unit) {
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
                            for (i in 0 until cast.rests.size) {
                                val stringValue = (state as Rest.Default).rests[i]
                                TimerButton(
                                    text = stringValue,
                                    onClick = { restViewModel.onDefaultTimerClick(stringValue) })
                            }
                        }
                    } else {
                        ListItemPicker(
                            dividersColor = Color.White,
                            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSecondaryContainer),
                            value = cast.pickerValue,
                            onValueChange = { restViewModel.updateNumberPicker(it) },
                            list = cast.rests
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