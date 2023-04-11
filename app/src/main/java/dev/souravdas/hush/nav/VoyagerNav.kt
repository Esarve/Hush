package dev.souravdas.hush.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import dev.souravdas.hush.compose.AboutScreen
import dev.souravdas.hush.compose.AppLogList
import dev.souravdas.hush.compose.SettingsPage
import dev.souravdas.hush.compose.main.Home

/**
 * Created by Sourav
 * On 3/31/2023 3:48 PM
 * For Hush!
 */


object HomeTab: Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "HOME"
            val icon = rememberVectorPainter(Icons.Rounded.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = "home",
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        Home()
    }
}

object LogTab: Tab{
    override val options: TabOptions
        @Composable
        get() {
            val title = "HOME"
            val icon = rememberVectorPainter(Icons.Rounded.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = "home",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        AppLogList()
    }

}

class SettingsScreen: Screen{
    @Composable
    override fun Content() {
        SettingsPage()
    }
}

class AboutScreen: Screen{
    @Composable
    override fun Content() {
        AboutScreen()
    }
}

class AppLogScreen(): Screen{
    @Composable
    override fun Content() {
        AppLogList()
    }

}