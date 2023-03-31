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

data class MainScreen(
    val checkNotificationPermission: () -> Boolean,
    val onNotificationPermissionGet: () -> Unit) : Screen{

    @Composable
    override fun Content() {
        MainActivityScreen(
            onNotificationPermissionGet = onNotificationPermissionGet,
            checkNotificationPermission = checkNotificationPermission
        )
    }
}

object SettingsScreen: Screen{
    @Composable
    override fun Content() {
        SettingsPage()
    }
}

object AboutScreen: Screen{
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