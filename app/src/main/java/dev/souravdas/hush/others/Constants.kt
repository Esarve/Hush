package dev.souravdas.hush.others

import android.widget.Toast
import dev.souravdas.hush.HushApp

/**
 * Created by Sourav
 * On 3/9/2023 12:54 PM
 * For Hush!
 */
object Constants {
    const val DS_HUSH_STATUS = "hush_status"
    const val DS_DND = "dnd"
    const val DS_DELETE_EXPIRE = "delete_expire"
    const val DS_NOTIFY_MUTE = "notify_mute"
    const val DS_NOTIFICATION_PERMISSION = "notific_perm"

    fun showNIY (){
        Toast.makeText(
            HushApp.context,
            "Not Implemented yet ¯\u2060\\\u2060_\u2060(\u2060ツ\u2060)\u2060_\u2060/\u2060¯",
            Toast.LENGTH_SHORT
        ).show()
    }
}