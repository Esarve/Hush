package dev.souravdas.hush.nav

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
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
        AppLogList(app_id, appName)

    }

}