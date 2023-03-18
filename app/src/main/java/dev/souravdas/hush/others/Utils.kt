package dev.souravdas.hush.others

import dev.souravdas.hush.models.SelectedApp
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sourav
 * On 3/11/2023 11:07 AM
 * For Hush!
 */

class Utils {

    fun isHushActive(selectedApp: SelectedApp):Boolean{
        return (System.currentTimeMillis() <= selectedApp.timeUpdated + selectedApp.durationInMinutes!!* 60000)
    }

    fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
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

    fun toLocalTime(stringTime: String): LocalTime {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(stringTime, formatter)
    }

    fun getStringFromDaysList(daysList: List<String?>): String {
        val sb = StringBuilder()
        for (item in daysList){
            if (!item.isNullOrEmpty()){
                sb.append(item).append(",")
            }
        }
        return if (sb.toString().isEmpty()) "" else sb.toString().substring(0, sb.length-1)
    }
}