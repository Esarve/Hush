package dev.souravdas.hush.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.destination
import dev.souravdas.hush.compose.destinations.AppLogListDestination
import dev.souravdas.hush.compose.destinations.Destination
import dev.souravdas.hush.compose.destinations.HomeDestination
import dev.souravdas.hush.nav.BottomNavigationDestination
import timber.log.Timber

/**
 * Created by Sourav
 * On 4/7/2023 3:03 PM
 * For Hush!
 */

@Composable
fun FloatingNav(onClickAdd: () -> Unit = {}, navController: NavController) {

    Card(
        modifier = Modifier
            .padding(bottom = 16.dp, start = 56.dp, end = 56.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val current = BottomNavigationDestination.HOME.direction
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        if (navController.currentBackStackEntry?.destination()  != HomeDestination) {
                            Timber.i("Navigating to Home")
                            navController.popBackStack()
                        }
                    }) {
                Icon(
                    Icons.Rounded.Home,
                    contentDescription = "icon",
                    tint = if (isCurrentDest(
                            navController = navController,
                            direction = current
                        )
                    ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                AnimatedVisibility(
                    isCurrentDest(
                        navController = navController,
                        direction = current
                    )
                ) {
                    Text(
                        text = "Home",
                        fontSize = 12.sp,
                        color = if (isCurrentDest(
                                navController = navController,
                                direction = current
                            )
                        ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }


            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .clip(CircleShape)
                    .clickable {
                        onClickAdd.invoke()
                        if(navController.currentBackStackEntry?.destination()  != HomeDestination){
                            navController.popBackStack()
                        }
                    }
                    .background(MaterialTheme.colorScheme.onPrimary)
            ) {
                Icon(
                    Icons.Rounded.Add, "ADD",
                    tint = MaterialTheme.colorScheme.primary,
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
                        if (navController.currentBackStackEntry?.destination()  != AppLogListDestination) {
                            Timber.d("Navigating to Logs")
                            navController.navigate(
                                BottomNavigationDestination.LOGS.direction,
                                fun NavOptionsBuilder.() {
                                    launchSingleTop = true
                                })
                        }
                    }) {
                val logTab = BottomNavigationDestination.LOGS.direction
                Icon(
                    Icons.Rounded.List,
                    contentDescription = "icon",
                    tint = if (isCurrentDest(
                            navController = navController,
                            direction = logTab
                        )
                    ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                AnimatedVisibility(
                    isCurrentDest(
                        navController = navController,
                        direction = logTab
                    )
                ) {
                    Text(
                        text = "Logs",
                        fontSize = 12.sp,
                        color = if (isCurrentDest(
                                navController = navController,
                                direction = logTab
                            )
                        ) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

        }
    }
}

@Composable
fun isCurrentDest(navController: NavController, direction: DirectionDestinationSpec): Boolean {
    val currentDestination: Destination =
        navController.appCurrentDestinationAsState().value ?: NavGraphs.root.startAppDestination
    return currentDestination == direction
}


