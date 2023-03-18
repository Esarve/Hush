package dev.souravdas.hush.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.souravdas.hush.compose.main.MainScreen

/**
 * Created by Sourav
 * On 3/18/2023 9:59 PM
 * For Hush!
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    checkNotificationPermission: () -> Boolean,
    onNotificationPermissionGet: () -> Unit
) {
    NavHost(
        navController,
        startDestination = Screens.MainScreen.route
    ) {
        composable(
            route = Screens.MainScreen.route,
        ) {
            MainScreen().MainActivityScreen(
                onNotificationPermissionGet = onNotificationPermissionGet,
                checkNotificationPermission = checkNotificationPermission
            )
        }
    }
}