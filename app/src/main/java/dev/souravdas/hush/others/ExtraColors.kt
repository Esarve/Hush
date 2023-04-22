package dev.souravdas.hush.others

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import dev.souravdas.hush.ui.theme.dark_Info
import dev.souravdas.hush.ui.theme.dark_InfoContainer
import dev.souravdas.hush.ui.theme.dark_onInfo
import dev.souravdas.hush.ui.theme.dark_onInfoContainer
import dev.souravdas.hush.ui.theme.dark_onwarning
import dev.souravdas.hush.ui.theme.dark_onwarningContainer
import dev.souravdas.hush.ui.theme.dark_warning
import dev.souravdas.hush.ui.theme.dark_warningContainer
import dev.souravdas.hush.ui.theme.light_Info
import dev.souravdas.hush.ui.theme.light_InfoContainer
import dev.souravdas.hush.ui.theme.light_onInfo
import dev.souravdas.hush.ui.theme.light_onInfoContainer
import dev.souravdas.hush.ui.theme.light_onwarning
import dev.souravdas.hush.ui.theme.light_onwarningContainer
import dev.souravdas.hush.ui.theme.light_warning
import dev.souravdas.hush.ui.theme.light_warningContainer

object ExtraColors {
    val info = ComposableColor(light_Info, dark_Info)
    val onInfo = ComposableColor(light_onInfo, dark_onInfo)
    val infoContainer = ComposableColor(light_InfoContainer, dark_InfoContainer)
    val onInfoContainer = ComposableColor(light_onInfoContainer, dark_onInfoContainer)
    val warning = ComposableColor(light_warning, dark_warning)
    val onWarning = ComposableColor(light_onwarning, dark_onwarning)
    val warningContainer = ComposableColor(light_warningContainer, dark_warningContainer)
    val onWarningContainer = ComposableColor(light_onwarningContainer, dark_onwarningContainer)

}

class ComposableColor(private val lightColor: Color, private val darkColor: Color) {

    fun getColor(isDarkTheme: Boolean): Color {
        return if (isDarkTheme) darkColor else lightColor
    }
}

val LocalColors = compositionLocalOf<ExtraColors> { error("No ColorPalette provided") }

@Composable
fun ProvideColors(content: @Composable () -> Unit) {
    val colors = ExtraColors
    CompositionLocalProvider(LocalColors provides colors, content = content)
}

@Composable
fun getColors(): ExtraColors = LocalColors.current