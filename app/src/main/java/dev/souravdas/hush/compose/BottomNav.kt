package dev.souravdas.hush.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import dev.souravdas.hush.nav.HomeTab
import dev.souravdas.hush.nav.LogTab

/**
 * Created by Sourav
 * On 4/7/2023 3:03 PM
 * For Hush!
 */

@Composable
fun FloatingNav(onClickAdd: () -> Unit = {}) {
    val tabNavigator = LocalTabNavigator.current
    val onTabClick = remember<(Tab) -> Unit> {
        {
            tabNavigator.current = it
        }
    }


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
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onTabClick(HomeTab)
                    }) {
                Icon(
                    Icons.Rounded.Home,
                    contentDescription = "icon",
                    tint = if (tabNavigator.current == HomeTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    color = if (tabNavigator.current == HomeTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant
                )
            }


            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp, 48.dp)
                    .clip(CircleShape)
                    .clickable { onClickAdd.invoke() }
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
                        onTabClick(LogTab)
                    }) {
                Icon(
                    Icons.Rounded.List,
                    contentDescription = "icon",
                    tint = if (tabNavigator.current == LogTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Logs",
                    fontSize = 12.sp,
                    color = if (tabNavigator.current == LogTab) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surfaceVariant
                )
            }

        }
    }

}

@Preview
@Composable
fun NavPrev() {
    FloatingNav()
}

