package dev.souravdas.hush.others

/**
 * Created by Sourav
 * On 3/21/2023 4:01 PM
 * For Hush!
 */
sealed class UIEvents{
    data class NotificationPermission(val isAvailable: Boolean): UIEvents()
}
