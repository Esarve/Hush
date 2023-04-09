package dev.souravdas.hush.models

/**
 * Created by Sourav
 * On 4/1/2023 11:42 AM
 * For Hush!
 */
sealed class UIEvent{
    object invokeNotificationPermissionCheck: UIEvent()
    object invokeNotificationPermissionGet: UIEvent()

    object showSelectAppSheet: UIEvent()
}
