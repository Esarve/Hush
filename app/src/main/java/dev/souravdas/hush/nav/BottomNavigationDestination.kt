package dev.souravdas.hush.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dev.souravdas.hush.compose.destinations.AppLogListDestination
import dev.souravdas.hush.compose.destinations.HomeDestination

/**
 * Created by Sourav
 * On 4/20/2023 8:39 PM
 * For Hush!
 */
enum class BottomNavigationDestination (
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String
){
    HOME(HomeDestination,Icons.Rounded.Home, "Home"),
    LOGS(AppLogListDestination, Icons.Rounded.List,"Logs")
}