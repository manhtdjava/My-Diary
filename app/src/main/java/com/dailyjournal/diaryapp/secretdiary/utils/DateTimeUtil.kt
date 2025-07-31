package com.holybible.biblestudy.dailybible.utils

import android.annotation.SuppressLint
import android.content.Context
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtil {
    fun formatDateDay(inputDate: String, context: Context): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale(SystemUtil.getPreLanguage(context)))
        val outputFormat = SimpleDateFormat("MMMM dd", Locale(SystemUtil.getPreLanguage(context)))

        try {
            val date = inputFormat.parse(inputDate)
            return date?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun formatDateYear(inputDate: String, context: Context): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale(SystemUtil.getPreLanguage(context)))
        val outputFormat = SimpleDateFormat("MMMM yyyy", Locale(SystemUtil.getPreLanguage(context)))

        try {
            val date = inputFormat.parse(inputDate)
            return date?.let { outputFormat.format(it) } ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun formatDatePray(dateString: String, context: Context): List<Int> {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale(SystemUtil.getPreLanguage(context)))
        try {
            val date = dateFormat.parse(dateString)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date

                val year = calendar.get(Calendar.YEAR)
                val month =
                    calendar.get(Calendar.MONTH) + 1 // Tháng trong Calendar được đếm từ 0 đến 11
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                return listOf(year, month, day)
            } else {
                println("Chuỗi ngày không hợp lệ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        return emptyList()
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDateTime(dateLong: Long): String {
        val date = Date(dateLong)
        val dateFormat = SimpleDateFormat("HH:mm:ss - dd/M/yyyy")
        return dateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDate(dateLong: Long): String {
        val date = Date(dateLong)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        return dateFormat.format(date)
    }

    @SuppressLint("NewApi")
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }

    @SuppressLint("NewApi")
    fun getCurrentDatePrayNow(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(formatter)
    }

    fun getCurrentDayOfWeek(context: Context): String {
        val calendar = Calendar.getInstance()
        val dayOfWeekString = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> context.getString(R.string.sunday)
            Calendar.MONDAY -> context.getString(R.string.monday)
            Calendar.TUESDAY -> context.getString(R.string.tuesday)
            Calendar.WEDNESDAY -> context.getString(R.string.wednesday)
            Calendar.THURSDAY -> context.getString(R.string.thursday)
            Calendar.FRIDAY -> context.getString(R.string.friday)
            Calendar.SATURDAY -> context.getString(R.string.saturday)
            else -> "Invalid day"
        }

        return dayOfWeekString
    }

    fun getCurrentDayOfWeekEng(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeekString = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sunday"
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            else -> "Invalid day"
        }

        return dayOfWeekString
    }

    fun getDaysOfWeek(): List<String> {
        val calendar = Calendar.getInstance()
        val daysOfWeek = mutableListOf<String>()

        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        for (i in 0 until 7) {
            val day = Calendar.getInstance()
            day.timeInMillis = calendar.timeInMillis
            daysOfWeek.add(formatDate(day.timeInMillis))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return daysOfWeek
    }

    fun checkTimePray(): Boolean {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        return currentHour in 6..17
    }
}