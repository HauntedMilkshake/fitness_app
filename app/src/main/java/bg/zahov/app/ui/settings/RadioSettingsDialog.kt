package bg.zahov.app.ui.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import bg.zahov.app.data.model.state.TypeSettings

@Composable
fun RadioSettingsDialog(
    type: TypeSettings,
    selected: Any,
    onDismissRequest: () -> Unit,
    setSelected: (type: TypeSettings, selected: Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest ) {
        Card(
            modifier = modifier
                .testTag("RadioSettings")
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = type.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopCenter),
                textAlign = TextAlign.Center,
            )
            LazyColumn {
                items(type.list) { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (item == selected),
                                onClick = { setSelected(type, item) }
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = item == selected,
                            onClick = { setSelected(type, item) })
                        Text(text = item.toString(), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}