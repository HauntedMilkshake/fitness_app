package bg.zahov.app.ui.loading

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import bg.zahov.fitness.app.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen() {
    LoadingScreenContent()
}

@Composable
fun LoadingScreenContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher),
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentSize()
        )

        AnimatedImage(
            resource = R.drawable.ic_bottom_left,
            modifier = Modifier.align(Alignment.BottomStart),
        )

        AnimatedImage(
            resource = R.drawable.ic_bottom_right,
            modifier = Modifier.align(Alignment.BottomEnd),
        )

        AnimatedImage(
            resource = R.drawable.ic_top_left,
            modifier = Modifier.align(Alignment.TopStart),
        )

        AnimatedImage(
            resource = R.drawable.ic_top_right,
            modifier = Modifier.align(Alignment.TopEnd),
        )
    }
}

@Composable
fun AnimatedImage(
    resource: Int,
    modifier: Modifier = Modifier,
    tintColor: Color = MaterialTheme.colorScheme.background,
    animationSpec: AnimationSpec<Float> = tween(durationMillis = 2000)
) {
    var scale by remember { mutableFloatStateOf(0f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = animationSpec,
        finishedListener = {}, label = ""
    )

    LaunchedEffect(Unit) {
        delay(100)
        scale = 10f
    }

    Image(
        painter = painterResource(id = resource),
        contentDescription = null,
        colorFilter = ColorFilter.tint(tintColor),
        modifier = modifier.graphicsLayer(
            scaleX = animatedScale,
            scaleY = animatedScale,
            transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
        )
    )
}