package com.dailyjournal.diaryapp.secretdiary.ui.notify

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityNotificationBinding
import com.dailyjournal.diaryapp.secretdiary.dialog.SetTimeDialog
import com.dailyjournal.diaryapp.secretdiary.receiver.DailyNotificationReceiver
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.visible
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class NotificationActivity : BaseActivity<ActivityNotificationBinding>() {

    override fun setViewBinding(): ActivityNotificationBinding {
        return ActivityNotificationBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        if (SharePrefUtils.getTheme(this) == 2 || SharePrefUtils.getTheme(
                this
            ) == 5
        ) {
            binding.tv.setTextColor(this.getColor(R.color.color_F4F5F5))
            binding.ivBack.setImageResource(R.drawable.ic_back_white)
        } else {
            binding.tv.setTextColor(this.getColor(R.color.color_292B2B))
            binding.ivBack.setImageResource(R.drawable.ic_back)
        }
        binding.root.setBackgroundResource(getBackground(SharePrefUtils.getTheme(this)))
        showView(SharePrefUtils.isNotify(this))
        showRepeat(SharePrefUtils.getRepeatNotify(this))
        binding.tvTime.text =
            SharePrefUtils.getSetTime(this@NotificationActivity).uppercase(Locale.ROOT)
                .replace("AM", "").replace("PM", "").trim()
    }

    override fun viewListener() {
        binding.apply {
            ivBack.tap { finish() }
            llSetTime.tap {
                val dialog = SetTimeDialog(
                    this@NotificationActivity,
                    this@NotificationActivity,
                    SharePrefUtils.getSetTime(this@NotificationActivity)
                ) {
                    SharePrefUtils.setSetTime(this@NotificationActivity, it)
                    Log.d("NotificationScheduler", "viewListener: " + it)
                    binding.tvTime.text =
                        it.uppercase(Locale.ROOT).replace("AM", "").replace("PM", "").trim()
                    setNotify()
                }
                dialog.show()

            }
            llRepeat.tap { popupMenu(binding.llRepeat, this@NotificationActivity) }
            rlNotify.tap {
                if (SharePrefUtils.isNotify(this@NotificationActivity)) {
                    showView(false)
                    SharePrefUtils.setNotify(
                        this@NotificationActivity, false
                    )
                    cancelNotification(this@NotificationActivity)

                } else {
                    showView(true)
                    SharePrefUtils.setNotify(
                        this@NotificationActivity, true
                    )
                    setNotify()
                }

            }
        }

    }

    private fun setNotify() {
        cancelNotification(this)
        if (SharePrefUtils.getRepeatNotify(this@NotificationActivity) == 0) {
            scheduleDailyNotification(
                this@NotificationActivity,
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(this@NotificationActivity))[0].toInt(),
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(this@NotificationActivity))[1].toInt()
            )
        } else {
            scheduleWeeklyNotificationV2(
                this@NotificationActivity,
                SharePrefUtils.getRepeatNotify(this@NotificationActivity),
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(this@NotificationActivity))[0].toInt(),
                extractHourAndMinuteAsString(SharePrefUtils.getSetTime(this@NotificationActivity))[1].toInt()
            )
        }
    }

    override fun dataObservable() {
    }

    private fun showRepeat(type: Int) {
        when (type) {
            0 -> {
                binding.tvRepeat.setText(R.string.every_day)
            }

            2 -> {
                binding.tvRepeat.setText(R.string.tv_Monday)
            }

            3 -> {
                binding.tvRepeat.setText(R.string.tv_Tuesday)
            }

            4 -> {
                binding.tvRepeat.setText(R.string.tv_Wednesday)
            }

            5 -> {
                binding.tvRepeat.setText(R.string.tv_Thursday)
            }

            6 -> {
                binding.tvRepeat.setText(R.string.tv_Friday)
            }

            7 -> {
                binding.tvRepeat.setText(R.string.tv_Saturday)
            }

            1 -> {
                binding.tvRepeat.setText(R.string.tv_Sunday)
            }
        }
    }

    private fun showView(isCheck: Boolean) {
        if (isCheck) {
            binding.ivNotify.gone()
            binding.ivSelectNotify.visible()
            binding.ll2.alpha = 1f
            binding.ll3.alpha = 1f
            binding.llSetTime.isEnabled = true
            binding.llRepeat.isEnabled = true
        } else {
            binding.ivNotify.visible()
            binding.ivSelectNotify.gone()
            binding.ll2.alpha = 0.6f
            binding.ll3.alpha = 0.6f
            binding.llSetTime.isEnabled = false
            binding.llRepeat.isEnabled = false
        }
    }

    //tất cả các ngày trong tuần
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
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
        Log.d("NotificationScheduler", "Notification set for: " + calendar.time)

    }

    fun rescheduleForNextDay(context: Context) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        scheduleDailyNotification(
            context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)
        )
    }

    //ngày thường
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleWeeklyNotificationV2(
        context: Context, dayOfWeek: Int, hour: Int, minute: Int
    ) {
        Log.d(
            "NotificationScheduler",
            "scheduleWeeklyNotificationV2: " + dayOfWeek + " " + hour + " " + minute
        )
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
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
        Log.d("NotificationScheduler", "Notification set for V2: " + calendar.time)
    }

    fun rescheduleForNextWeek(context: Context) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        scheduleWeeklyNotificationV2(
            context,
            calendar.get(Calendar.DAY_OF_WEEK),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )
    }

    fun cancelNotification(context: Context) {
        val intent = Intent(context, DailyNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        Log.d("NotificationScheduler", "Notification canceled")
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

    @SuppressLint("ResourceAsColor")
    fun popupMenu(
        btnClick: View,
        context: Context,
    ) {

        val location = IntArray(2)
        btnClick.getLocationOnScreen(location)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.popup_layout_repeat, null)
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupWindow = PopupWindow(context)
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.contentView = popupView
//        popupWindow.width = (context.resources.displayMetrics.widthPixels * 0.3).toInt()
        popupWindow.isFocusable = true
        val x = location[0] - ((popupView.width - btnClick.width) / 2) - 400
        val y = location[1] + btnClick.height - 70
        popupWindow.showAtLocation(btnClick, Gravity.NO_GRAVITY, x, y)
        val ctl1 = popupView.findViewById<ConstraintLayout>(R.id.ctl_1)
        val ctl2 = popupView.findViewById<ConstraintLayout>(R.id.ctl_2)
        val ctl3 = popupView.findViewById<ConstraintLayout>(R.id.ctl_3)
        val ctl4 = popupView.findViewById<ConstraintLayout>(R.id.ctl_4)
        val ctl5 = popupView.findViewById<ConstraintLayout>(R.id.ctl_5)
        val ctl6 = popupView.findViewById<ConstraintLayout>(R.id.ctl_6)
        val ctl7 = popupView.findViewById<ConstraintLayout>(R.id.ctl_7)
        val ctl8 = popupView.findViewById<ConstraintLayout>(R.id.ctl_8)

        val icBox1 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_1)
        val icBox2 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_2)
        val icBox3 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_3)
        val icBox4 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_4)
        val icBox5 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_5)
        val icBox6 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_6)
        val icBox7 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_7)
        val icBox8 = popupView.findViewById<AppCompatImageView>(R.id.ic_box_8)

        val tvDay1 = popupView.findViewById<TextView>(R.id.tv_day_1)
        val tvDay2 = popupView.findViewById<TextView>(R.id.tv_day_2)
        val tvDay3 = popupView.findViewById<TextView>(R.id.tv_day_3)
        val tvDay4 = popupView.findViewById<TextView>(R.id.tv_day_4)
        val tvDay5 = popupView.findViewById<TextView>(R.id.tv_day_5)
        val tvDay6 = popupView.findViewById<TextView>(R.id.tv_day_6)
        val tvDay7 = popupView.findViewById<TextView>(R.id.tv_day_7)
        val tvDay8 = popupView.findViewById<TextView>(R.id.tv_day_8)

        ctl1.setBackgroundResource(R.color.color_white)
        ctl2.setBackgroundResource(R.color.color_white)
        ctl3.setBackgroundResource(R.color.color_white)
        ctl4.setBackgroundResource(R.color.color_white)
        ctl5.setBackgroundResource(R.color.color_white)
        ctl6.setBackgroundResource(R.color.color_white)
        ctl7.setBackgroundResource(R.color.color_white)
        ctl8.setBackgroundResource(R.color.color_white)

        icBox1.setImageResource(R.drawable.ic_no_select_day)
        icBox2.setImageResource(R.drawable.ic_no_select_day)
        icBox3.setImageResource(R.drawable.ic_no_select_day)
        icBox4.setImageResource(R.drawable.ic_no_select_day)
        icBox5.setImageResource(R.drawable.ic_no_select_day)
        icBox6.setImageResource(R.drawable.ic_no_select_day)
        icBox7.setImageResource(R.drawable.ic_no_select_day)
        icBox8.setImageResource(R.drawable.ic_no_select_day)

        tvDay1.setTextColor(R.color.color_D9000000)
        tvDay2.setTextColor(R.color.color_D9000000)
        tvDay3.setTextColor(R.color.color_D9000000)
        tvDay4.setTextColor(R.color.color_D9000000)
        tvDay5.setTextColor(R.color.color_D9000000)
        tvDay6.setTextColor(R.color.color_D9000000)
        tvDay7.setTextColor(R.color.color_D9000000)
        tvDay8.setTextColor(R.color.color_D9000000)

        when (SharePrefUtils.getRepeatNotify(context)) {
            0 -> {
                ctl1.setBackgroundResource(R.color.color_F3FBFF)
                icBox1.setImageResource(R.drawable.ic_select_day)
                tvDay1.setTextColor(context.getColor(R.color.color_FF7375))
            }

            2 -> {
                ctl2.setBackgroundResource(R.color.color_F3FBFF)
                icBox2.setImageResource(R.drawable.ic_select_day)
                tvDay2.setTextColor(context.getColor(R.color.color_FF7375))
            }

            3 -> {
                ctl3.setBackgroundResource(R.color.color_F3FBFF)
                icBox3.setImageResource(R.drawable.ic_select_day)
                tvDay3.setTextColor(context.getColor(R.color.color_FF7375))
            }

            4 -> {
                ctl4.setBackgroundResource(R.color.color_F3FBFF)
                icBox4.setImageResource(R.drawable.ic_select_day)
                tvDay4.setTextColor(context.getColor(R.color.color_FF7375))
            }

            5 -> {
                ctl5.setBackgroundResource(R.color.color_F3FBFF)
                icBox5.setImageResource(R.drawable.ic_select_day)
                tvDay5.setTextColor(context.getColor(R.color.color_FF7375))
            }

            6 -> {
                ctl6.setBackgroundResource(R.color.color_F3FBFF)
                icBox6.setImageResource(R.drawable.ic_select_day)
                tvDay6.setTextColor(context.getColor(R.color.color_FF7375))
            }

            7 -> {
                ctl7.setBackgroundResource(R.color.color_F3FBFF)
                icBox7.setImageResource(R.drawable.ic_select_day)
                tvDay7.setTextColor(context.getColor(R.color.color_FF7375))
            }

            1 -> {
                ctl8.setBackgroundResource(R.color.color_F3FBFF)
                icBox8.setImageResource(R.drawable.ic_select_day)
                tvDay8.setTextColor(context.getColor(R.color.color_FF7375))
            }
        }

        ctl1.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl1.setBackgroundResource(R.color.color_F3FBFF)
            icBox1.setImageResource(R.drawable.ic_select_day)
            tvDay1.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 0)
            showRepeat(0)
            popupWindow.dismiss()
            setNotify()
        }
        ctl2.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl2.setBackgroundResource(R.color.color_F3FBFF)
            icBox2.setImageResource(R.drawable.ic_select_day)
            tvDay2.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 2)
            showRepeat(2)
            popupWindow.dismiss()
            setNotify()
        }
        ctl3.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl3.setBackgroundResource(R.color.color_F3FBFF)
            icBox3.setImageResource(R.drawable.ic_select_day)
            tvDay3.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 3)
            showRepeat(3)
            popupWindow.dismiss()
            setNotify()
        }
        ctl4.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl4.setBackgroundResource(R.color.color_F3FBFF)
            icBox4.setImageResource(R.drawable.ic_select_day)
            tvDay4.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 4)
            showRepeat(4)
            popupWindow.dismiss()
            setNotify()
        }
        ctl5.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl5.setBackgroundResource(R.color.color_F3FBFF)
            icBox5.setImageResource(R.drawable.ic_select_day)
            tvDay5.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 5)
            showRepeat(5)
            popupWindow.dismiss()
            setNotify()
        }
        ctl6.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl6.setBackgroundResource(R.color.color_F3FBFF)
            icBox6.setImageResource(R.drawable.ic_select_day)
            tvDay6.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 6)
            showRepeat(6)
            popupWindow.dismiss()
            setNotify()
        }
        ctl7.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl7.setBackgroundResource(R.color.color_F3FBFF)
            icBox7.setImageResource(R.drawable.ic_select_day)
            tvDay7.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 7)
            showRepeat(7)
            popupWindow.dismiss()
            setNotify()
        }
        ctl8.tap {
            ctl1.setBackgroundResource(R.color.color_white)
            ctl2.setBackgroundResource(R.color.color_white)
            ctl3.setBackgroundResource(R.color.color_white)
            ctl4.setBackgroundResource(R.color.color_white)
            ctl5.setBackgroundResource(R.color.color_white)
            ctl6.setBackgroundResource(R.color.color_white)
            ctl7.setBackgroundResource(R.color.color_white)
            ctl8.setBackgroundResource(R.color.color_white)
            icBox1.setImageResource(R.drawable.ic_no_select_day)
            icBox2.setImageResource(R.drawable.ic_no_select_day)
            icBox3.setImageResource(R.drawable.ic_no_select_day)
            icBox4.setImageResource(R.drawable.ic_no_select_day)
            icBox5.setImageResource(R.drawable.ic_no_select_day)
            icBox6.setImageResource(R.drawable.ic_no_select_day)
            icBox7.setImageResource(R.drawable.ic_no_select_day)
            icBox8.setImageResource(R.drawable.ic_no_select_day)
            tvDay1.setTextColor(R.color.color_D9000000)
            tvDay2.setTextColor(R.color.color_D9000000)
            tvDay3.setTextColor(R.color.color_D9000000)
            tvDay4.setTextColor(R.color.color_D9000000)
            tvDay5.setTextColor(R.color.color_D9000000)
            tvDay6.setTextColor(R.color.color_D9000000)
            tvDay7.setTextColor(R.color.color_D9000000)
            tvDay8.setTextColor(R.color.color_D9000000)

            ctl8.setBackgroundResource(R.color.color_F3FBFF)
            icBox8.setImageResource(R.drawable.ic_select_day)
            tvDay8.setTextColor(R.color.color_FF7375)
            SharePrefUtils.setRepeatNotify(context, 1)
            showRepeat(1)
            popupWindow.dismiss()
            setNotify()
        }

    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    this, SharePrefRemote.native_mine
                ) && AdsConsentManager.getConsentResult(this)
            ) {
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    this,
                    binding.frAds,
                    R.layout.ads_native_large_top_shimer,
                    R.layout.layout_native_large_top,
                    R.layout.layout_native_large_top
                )
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_mine"))
                val nativeManager = NativeManager(
                    this, this, nativeBuilder
                ).setIntervalReloadNative(Contants.interval_reload_native)

            } else {
                binding.frAds.visibility = View.GONE
                binding.frAds.removeAllViews()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
        }
    }

}