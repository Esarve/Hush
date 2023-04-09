package dev.souravdas.hush.nav

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import dev.souravdas.hush.compose.AboutScreen
import dev.souravdas.hush.compose.AppLogList
import dev.souravdas.hush.compose.SettingsPage
import dev.souravdas.hush.compose.main.MainActivityScreen

/**
 * Created by Sourav
 * On 3/31/2023 3:48 PM
 * For Hush!
 */

class MainScreen() : Screen{

    @Composable
    override fun Content() {
        MainActivityScreen()
    }
}

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
        Navigator(MainScreen()){
            SlideTransition(navigator = it)
        }
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

    @OptIn(ExperimentalAnimationApi::class)
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

data class AppLogScreen(
    val app_id: Long?,
    val appName: String?,): Screen{
    @Composable
    override fun Content() {
        AppLogList()

    }

}