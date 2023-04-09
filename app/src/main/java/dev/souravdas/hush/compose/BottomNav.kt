package dev.souravdas.hush.compose

import android.graphics.BlurMaskFilter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.souravdas.hush.nav.HomeTab
import dev.souravdas.hush.nav.LogTab

/**
 * Created by Sourav
 * On 4/7/2023 3:03 PM
 * For Hush!
 */

@Composable
fun FloatingNav(onClickAdd: () -> Unit = {}) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current

    Row(
        modifier = Modifier
            .padding(bottom = 16.dp, start = 40.dp, end = 40.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(48.dp, 48.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    tabNavigator.current = HomeTab
                }) {
            Icon(
                Icons.Rounded.Home,
                contentDescription = "icon",
                tint = if (selected == HomeTab) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            this@Row.AnimatedVisibility(
                visible = selected == HomeTab,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    color = if (selected == HomeTab) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }


        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp, 48.dp)
                .clip(CircleShape)
                .clickable { onClickAdd.invoke() }
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.Rounded.Add, "ADD",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp, 32.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(48.dp, 48.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    tabNavigator.current = LogTab
                }) {
            Icon(
                Icons.Rounded.List,
                contentDescription = "icon",
                tint = if (selected == LogTab) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            this@Row.AnimatedVisibility(
                visible = selected == LogTab,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = "Logs",
                    fontSize = 12.sp,
                    color = if (selected == LogTab) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

    }
}

@Preview
@Composable
fun NavPrev() {
    FloatingNav()
}

fun Modifier.shadow(
    color: Color = Color.Black,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
    blurRadius: Dp = 0.dp,
) = then(
    drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }
            frameworkPaint.color = color.toArgb()

            val leftPixel = offsetX.toPx()
            val topPixel = offsetY.toPx()
            val rightPixel = size.width + topPixel
            val bottomPixel = size.height + leftPixel

            canvas.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                60f, 60f,
                paint = paint,
            )
        }
    }
)
