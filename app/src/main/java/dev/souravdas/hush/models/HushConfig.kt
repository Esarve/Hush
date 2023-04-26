package dev.souravdas.hush.models

/**
 * Created by Sourav
 * On 3/22/2023 3:02 PM
 * For Hush!
 */
class HushConfig {
    var isDnd: Boolean = false
    var isAutoDeleteExpired: Boolean = false
    var isNotificationReminder: Boolean = false

    constructor(isDnd: Boolean, isAutoDeleteExpired: Boolean, isNotificationReminder: Boolean) {
        this.isDnd = isDnd
        this.isAutoDeleteExpired = isAutoDeleteExpired
        this.isNotificationReminder = isNotificationReminder
    }

    constructor()

}