package bg.zahov.app.ui.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.theme.FitnessTheme
import bg.zahov.fitness.app.R
import kotlinx.coroutines.delay

@Composable
fun ShuttingDownScreen(viewModel: ShuttingDownViewModel = viewModel()) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.value) {
        delay(1000)
        viewModel.lowerCountDown()
    }
    ShuttingDownContent(timer = uiState.value)
}

@Preview
@Composable
fun ShuttingDownContent(modifier: Modifier = Modifier, timer: Int = 0) {
    FitnessTheme {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(120.dp),
                painter = painterResource(R.drawable.ic_error),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(R.string.application_warning_text),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Visible
            )
            Text(
                text = timer.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}