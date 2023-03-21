package dev.souravdas.hush.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.souravdas.hush.compose.AppLogList
import dev.souravdas.hush.compose.main.MainActivityScreen

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
            MainActivityScreen(
                navController,
                onNotificationPermissionGet = onNotificationPermissionGet,
                checkNotificationPermission = checkNotificationPermission
            )
        }

        composable(
            route = Screens.LogScreen.route,
            arguments = listOf(navArgument("app_id"){
                type = NavType.LongType
            })
        ) {
            AppLogList(it.arguments?.getLong("app_id"))
        }
    }
}