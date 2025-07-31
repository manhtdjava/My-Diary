package com.dailyjournal.diaryapp.secretdiary.receiver

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.core.app.NotificationCompat
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification =
            NotificationCompat.Builder(context, "channel1").setSmallIcon(R.drawable.logo)
                .setContentTitle(intent.getStringExtra("Notification"))
                .setContentText(intent.getStringExtra("Messages"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL).setAutoCancel(true).build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
        if (SharePrefUtils.getRepeatNotify(context) == 0) {
            scheduleDailyNotification(
                context,
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(context))[0].toInt(),
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(context))[1].toInt()
            )
        } else {
            scheduleWeeklyNotificationV2(
                context,
                SharePrefUtils.getRepeatNotify(context),
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(context))[0].toInt(),
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(context))[1].toInt()
            )
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleDailyNotification(
        context: Context, hour: Int, minute: Int
    ) {
        val intent = Intent(context, DailyNotificationReceiver::class.java)
        intent.putExtra("Notification", context.getString(R.string.notification))
        intent.putExtra("Messages", context.getString(R.string.title_mess_notify))
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
        Log.d("NotificationScheduler1", "Notification set for: " + calendar.time)

    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleWeeklyNotificationV2(
        context: Context, dayOfWeek: Int, hour: Int, minute: Int
    ) {
        val intent = Intent(context, DailyNotificationReceiver::class.java)
        intent.putExtra("Notification", context.getString(R.string.notification))
        intent.putExtra("Messages", context.getString(R.string.title_mess_notify))
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dayOfWeek,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
        Log.d("NotificationScheduler1", "Notification set for V2: " + calendar.time)
    }

    private fun extractHourAndMinuteAsString(timeString: String?): Array<String> {
        val inputFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH)
        val time: LocalTime = LocalTime.parse(timeString, inputFormatter)
        val hour: String = time.hour.toString()
        val minute: String

        if (time.minute == 0) {
            minute = time.minute.toString()
        } else {
            minute = time.minute.toString()
        }
        Log.d("NotificationScheduler", "extractHourAndMinuteAsString: " + hour + " " + minute)
        return arrayOf(hour, minute)
    }
}