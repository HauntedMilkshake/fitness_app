package bg.zahov.app.ui.custom

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import bg.zahov.fitness.app.R

@Composable
fun CommonDivider(modifier: Modifier = Modifier , color: Color? = null) = HorizontalDivider(
    modifier = modifier
        .fillMaxWidth()
        .height(1.dp)
        .padding(top = 4.dp, bottom = 16.dp),
    color = color?: MaterialTheme.colorScheme.onBackground
)