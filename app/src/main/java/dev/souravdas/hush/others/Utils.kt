package dev.souravdas.hush.others

import dev.souravdas.hush.models.SelectedApp
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        val fixTime = fixTimeString(stringTime)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return LocalTime.parse(fixTime, formatter)
    }

    private fun fixTimeString(timeStr: String): String {
        val (hours, minutes) = timeStr.split(":")
        val fixedHours = if (hours.length == 1) "0$hours" else hours
        val fixedMinutes = if (minutes.length == 1) "0$minutes" else minutes
        return "$fixedHours:$fixedMinutes"
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

    fun getStringDateFromMillis(millis: Long): String{
        val date = Date(millis)
        val sdf = SimpleDateFormat("dd-MM-yy hh:mm a", Locale.UK)
        return sdf.format(date)
    }

    fun getTimeAgo(dateTime: OffsetDateTime): String {
        val now = OffsetDateTime.now()

        val duration = Duration.between(dateTime, now)
        val seconds = duration.seconds

        return when {
            seconds < 60 -> "$seconds seconds ago"
            seconds < 3600 -> {
                val minutes = seconds / 60
                "$minutes minutes ago"
            }
            seconds < 86400 -> {
                val hours = seconds / 3600
                "$hours hours ago"
            }
            else -> {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                dateTime.format(formatter)
            }
        }
    }
}