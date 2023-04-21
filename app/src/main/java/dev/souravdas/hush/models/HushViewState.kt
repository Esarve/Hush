package dev.souravdas.hush.models

import de.palm.composestateevents.StateEvent
import de.palm.composestateevents.StateEventWithContent
import de.palm.composestateevents.consumed

/**
 * Created by Sourav
 * On 4/21/2023 12:16 PM
 * For Hush!
 */
data class HushViewState(
    var isNotificationAccessProvided: Boolean = true,
    val processNotificationAccessPermissionGet: StateEvent = consumed,
    val processSettingsScreenOpen: StateEvent = consumed,
    val startHushService: StateEventWithContent<Boolean> = consumed()
)
