package dev.souravdas.hush.nav

import androidx.compose.ui.window.DialogProperties
import com.ramcosta.composedestinations.spec.DestinationStyle

/**
 * Created by Sourav
 * On 4/21/2023 6:41 PM
 * For Hush!
 */

object NonDismissableDialog : DestinationStyle.Dialog {
    override val properties = DialogProperties(
        dismissOnClickOutside = false,
        dismissOnBackPress = false,
    )
}