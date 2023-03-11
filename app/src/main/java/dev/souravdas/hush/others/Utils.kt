package dev.souravdas.hush.others

import dev.souravdas.hush.models.SelectedApp
import javax.inject.Inject

/**
 * Created by Sourav
 * On 3/11/2023 11:07 AM
 * For Hush!
 */

class Utils @Inject constructor() {


    fun checkIfAppExpired(selectedApp: SelectedApp):Boolean{
        return (System.currentTimeMillis() <= selectedApp.timeUpdated + selectedApp.durationInMinutes!!* 60000)
    }
}