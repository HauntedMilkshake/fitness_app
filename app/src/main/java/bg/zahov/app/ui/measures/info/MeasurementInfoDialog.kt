package bg.zahov.app.ui.measures.info

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.traceEventEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.fitness.app.R

@Composable
fun MeasurementInfoDialog(
    modifier: Modifier = Modifier,
    viewModel: MeasurementInfoViewModel = viewModel(),
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = modifier) {
            Column {
                CommonTextField(
                    text = "",
                    label = {},
                    singleLine = true,
                    onTextChange = { viewModel.onHistoryInputChange(it) })
                Button(onClick = {}) {
                    Text(text = stringResource(R.string.confirm))
                }
            }
        }
    }
}