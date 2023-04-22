package dev.souravdas.hush.compose.main

import android.annotation.SuppressLint
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import de.palm.composestateevents.EventEffect
import dev.souravdas.hush.arch.MainActivityVM
import dev.souravdas.hush.compose.FloatingNav
import dev.souravdas.hush.compose.NavGraphs
import dev.souravdas.hush.compose.destinations.HomeDestination
import dev.souravdas.hush.compose.destinations.PermissionScreenDestination
import dev.souravdas.hush.compose.destinations.SettingsPageDestination
import dev.souravdas.hush.models.SelectedApp
import dev.souravdas.hush.nav.Layer2graph
import kotlinx.coroutines.launch

/**
 * Created by Sourav
 * On 4/20/2023 6:52 PM
 * For Hush!
 */

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn( ExperimentalAnimationApi::class)
@Layer2graph(true)
@Destination()
@Composable
fun MainScreen(viewModel: MainActivityVM, navigator: DestinationsNavigator) {
    viewModel.getSelectedApp()
    val uiState = viewModel.uiEventFlow.collectAsState()
    val scope = rememberCoroutineScope()

    val showBottomSheet = remember {
        mutableStateOf(false)
    }


    val installedApps = remember {
        viewModel.getPackageList()
    }


    val addSelectedApp = remember<(SelectedApp) -> Unit> {
        {
            viewModel.addOrUpdateSelectedApp(selectedApp = it)
            viewModel.getSelectedApp()
        }
    }

    val isBottomSheetOpenLambda = remember {
        {
            showBottomSheet.value
        }
    }

    EventEffect(
        event = uiState.value.processSettingsScreenOpen,
        onConsumed = viewModel::onConsumedSettingsOpen) {
        navigator.navigate(SettingsPageDestination())
    }

    ShowBottomSheet(installedApps, isBottomSheetOpenLambda) { app ->
        showBottomSheet.value = false
        app?.let {
            addSelectedApp.invoke(it)
        }
    }

    val navController = rememberAnimatedNavController()
    val notificationAccessProvided = remember {
        mutableStateOf(viewModel.isNotificationAccessPermissionProvided())
    }
    Text(text = "MAIN SCREEN")
    if (notificationAccessProvided.value)
        Scaffold(
            bottomBar = {
                FloatingNav({
                    scope.launch {
                        showBottomSheet.value = true
                    }
                }, navController)
            },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {

            DestinationsNavHost(
                navGraph = NavGraphs.root,
                navController = navController,
                dependenciesContainerBuilder = {
                    dependency(HomeDestination) {
                        viewModel
                    }
                })
        }
    else{
        navigator.popBackStack()
        navigator.navigate(PermissionScreenDestination){
           launchSingleTop = true
        }
    }
}