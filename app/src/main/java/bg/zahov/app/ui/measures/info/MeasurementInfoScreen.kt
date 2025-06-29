package bg.zahov.app.ui.measures.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bg.zahov.app.data.model.LineChartData
import bg.zahov.app.ui.custom.CommonLineChart
import bg.zahov.fitness.app.R

@Composable
fun MeasurementInfoScreen(viewModel: MeasurementInfoViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val inputState by viewModel.dialogState.collectAsStateWithLifecycle()

    MeasurementInfoContent(
        data = uiState.data,
        onAddHistoryClick = { viewModel.changeShowDialog() })
    when {
        uiState.loading -> CircularProgressIndicator(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        uiState.showDialog -> MeasurementInfoDialog(
            text = inputState,
            title = uiState.dataType,
            onDismissRequest = { viewModel.changeShowDialog() },
            onInputChange = { viewModel.onHistoryInputChange(it) },
            onSaveChange = { viewModel.saveInput() })
    }
}

@Composable
fun MeasurementInfoContent(
    modifier: Modifier = Modifier,
    data: LineChartData,
    onAddHistoryClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(20.dp)) {
        CommonLineChart(data = data)
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.history),
                style = MaterialTheme.typography.titleLarge,
                color = colorResource(R.color.white)
            )

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onAddHistoryClick
                    ),
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.add_history)
            )
        }
    }
}