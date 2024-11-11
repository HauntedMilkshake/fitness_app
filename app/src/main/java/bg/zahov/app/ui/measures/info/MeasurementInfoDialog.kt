package bg.zahov.app.ui.measures.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import bg.zahov.app.ui.custom.CommonTextField
import bg.zahov.fitness.app.R

@Composable
fun MeasurementInfoDialog(
    title: String,
    text:String,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onInputChange: (String) -> Unit,
    onSaveChange: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier,
            colors = CardColors(
                containerColor = colorResource(R.color.background),
                contentColor = colorResource(R.color.white),
                disabledContentColor = colorResource(R.color.text),
                disabledContainerColor = colorResource(R.color.disabled_button)
            )
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.white)
                )
                CommonTextField(
                    text = text,
                    label = {},
                    singleLine = true,
                    onTextChange = { onInputChange(it) }
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = onDismissRequest,
                        colors = ButtonColors(
                            contentColor = colorResource(R.color.white),
                            containerColor = colorResource(R.color.input_field_background),
                            disabledContainerColor = colorResource(R.color.disabled_button),
                            disabledContentColor = colorResource(R.color.disabled_button)
                        )
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                    Button(
                        onClick = onSaveChange, colors = ButtonColors(
                            contentColor = colorResource(R.color.white),
                            containerColor = colorResource(R.color.input_field_background),
                            disabledContainerColor = colorResource(R.color.disabled_button),
                            disabledContentColor = colorResource(R.color.disabled_button)
                        )
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}