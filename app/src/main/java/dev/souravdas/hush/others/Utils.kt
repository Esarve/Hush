package dev.souravdas.hush.others

import dev.souravdas.hush.models.SelectedApp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sourav
 * On 3/11/2023 11:07 AM
 * For Hush!
 */

class Utils {

    fun checkIfAppExpired(selectedApp: SelectedApp):Boolean{
        return (System.currentTimeMillis() <= selectedApp.timeUpdated + selectedApp.durationInMinutes!!* 60000)
    }

    fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> ""
        }
    }

    fun getCurrentTimeIn24Hours(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        return sdf.format(currentTime)
    }

    fun get12HrsFrom24Hrs (inputTime: String):String{
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val time = timeFormat.parse(inputTime)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return outputFormat.format(time)

    }
}