package dev.souravdas.hush.compose.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.souravdas.hush.HushApp
import dev.souravdas.hush.R

/**
 * Created by Sourav
 * On 3/18/2023 12:27 PM
 * For Hush!
 */

@Composable
fun ShowAlertDialog(
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                Icons.Rounded.Warning,
                contentDescription = null,
                modifier = Modifier.size(56.dp)
            )
        },
        title = {
            Text(text = "Notification Permission Necessary")
        },
        text = {
            Text(text = stringResource(id = R.string.notification_alert_dialog_body))
        },
        confirmButton = {
            Button(onClick = {
                Toast.makeText(
                    HushApp.context,
                    "Please Select Hush! from the list",
                    Toast.LENGTH_LONG
                ).show()
            }) {
                Text(text = "Allow")
            }
        },
        tonalElevation = 10.dp,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTimePicker(
    time: Pair<Int, Int>,
    title: String,
    onTimeSelected: (String) -> Unit,
    onDialogDismiss: () -> Unit
) {
    val state = rememberTimePickerState(time.first, time.second)

    Dialog(
        onDismissRequest = { onDialogDismiss.invoke() }, properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column(
                Modifier.background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                TimePicker(state = state)
                AddCancelButtonBar(onAddClick = {
                    onTimeSelected.invoke(state.hour.toString() + ":" + state.minute.toString())
                }, onCancelClick = {
                    onDialogDismiss.invoke()
                })
            }
        }
    }
}


@Composable
fun AddCancelButtonBar(
    onAddClick: () -> Unit,
    onCancelClick: () -> Unit = {}
) {
    Row() {
        val modifier =
            Modifier.padding(vertical =  8.dp)
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Cancel",
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = modifier
                .clickable {
                    onCancelClick.invoke()
                }
                .padding(8.dp))

        Spacer(modifier = Modifier.weight(0.05f))
        Text(text = "Add",
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = modifier
                .clickable {
                    onAddClick.invoke()
                }
                .padding(8.dp))
    }
}
