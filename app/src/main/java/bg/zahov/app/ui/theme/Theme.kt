package bg.zahov.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = background,
    secondary = less_vibrant_text,
    onPrimary = less_vibrant_text,
    onSecondary = white,
    background = background,
    primaryContainer = blue_button,
    onPrimaryContainer = blue_text,
    onErrorContainer = disabled_button
)

/**
 * currently we do not have colors for the white theme so we only use dark theme
 */
@Composable
fun FitnessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}