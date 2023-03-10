package dev.souravdas.hush.models

import android.graphics.drawable.Drawable
import dev.souravdas.hush.models.SelectedApp
import java.io.Serializable

/**
 * Created by Sourav
 * On 2/27/2023 1:18 PM
 * For Hush!
 */
data class SelectedAppForList(val selectedApp: SelectedApp, val icon: Drawable?) : Serializable
