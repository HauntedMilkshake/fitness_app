package bg.zahov.app.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import bg.zahov.app.ui.history.TextWithLeadingIcon
import bg.zahov.fitness.app.R


@Composable
fun WorkoutStats(
    modifier: Modifier = Modifier.fillMaxWidth(),
    duration: String,
    volume: String,
    personalRecords: String,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TextWithLeadingIcon(
            text = duration,
            icon = painterResource(R.drawable.ic_clock),
            textModifier = Modifier
                .weight(1f)
                .padding(4.dp),
            iconColor = MaterialTheme.colorScheme.secondary,
            iconModifier = Modifier.align(Alignment.CenterVertically)
        )
        TextWithLeadingIcon(
            text = stringResource(
                R.string.volume_for_history_workouts,
                volume
            ),
            icon = painterResource(R.drawable.ic_volume),
            textModifier = Modifier
                .weight(1f)
                .padding(4.dp),
            iconColor = MaterialTheme.colorScheme.secondary,
            iconModifier = Modifier.align(Alignment.CenterVertically)
        )
        TextWithLeadingIcon(
            text = personalRecords,
            icon = painterResource(R.drawable.ic_trophy),
            textModifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(4.dp),
            iconColor = MaterialTheme.colorScheme.secondary,
            iconModifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
