package dev.souravdas.hush.nav

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle
import dev.souravdas.hush.compose.appDestination
import dev.souravdas.hush.compose.destinations.AboutScreenDestination
import dev.souravdas.hush.compose.destinations.AppLogListDestination
import dev.souravdas.hush.compose.destinations.HomeDestination
import dev.souravdas.hush.compose.destinations.MainScreenDestination
import dev.souravdas.hush.compose.destinations.PermissionScreenDestination
import dev.souravdas.hush.compose.destinations.SettingsPageDestination

/**
 * Created by Sourav
 * On 4/25/2023 12:18 PM
 * For Hush!
 */

@OptIn(ExperimentalAnimationApi::class)
object TransitionAnimation : DestinationStyle.Animated {

    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            SettingsPageDestination, MainScreenDestination, AboutScreenDestination, PermissionScreenDestination ->
                slideInVertically (
                    initialOffsetY = { it },
                    animationSpec = tween(700)
                )
            HomeDestination, AppLogListDestination ->  {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(500)
                )
            }
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            SettingsPageDestination, MainScreenDestination, AboutScreenDestination, PermissionScreenDestination->
                slideOutVertically  (
                    targetOffsetY = { -it },
                    animationSpec = tween(700)
                )
            HomeDestination, AppLogListDestination ->  {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(500)
                )
            }
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when (initialState.appDestination()) {
            SettingsPageDestination, MainScreenDestination, AboutScreenDestination, PermissionScreenDestination->
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(700)
                )
            HomeDestination, AppLogListDestination ->  {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(500)
                )
            }
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when (targetState.appDestination()) {
            SettingsPageDestination, MainScreenDestination, AboutScreenDestination, PermissionScreenDestination ->
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(700)
                )
            HomeDestination, AppLogListDestination ->  {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(500)
                )
            }
            else -> null
        }
    }
}