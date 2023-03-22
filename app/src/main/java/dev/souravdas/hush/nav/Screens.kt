package dev.souravdas.hush.nav

/**
 * Created by Sourav
 * On 3/18/2023 9:56 PM
 * For Hush!
 */
sealed class Screens(val route: String){
    object MainScreen: Screens(route = "main_screen")
    object LogScreen: Screens(route = "log_screen/{app_id}/{app_name}")
    object SettingsScreen: Screens(route = "settings_screen")

}
