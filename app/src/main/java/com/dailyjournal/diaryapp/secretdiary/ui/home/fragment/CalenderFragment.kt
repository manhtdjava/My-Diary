package com.dailyjournal.diaryapp.secretdiary.ui.home.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.data.DiaryDao
import com.dailyjournal.diaryapp.secretdiary.data.DiaryDatabase.Companion.getDatabase
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.FragmentCalendarBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.model.DayModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.CreateGetActivity
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.DiaryCalenderAdapter
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.viewmodel.DiaryViewModel
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.visible
import com.holybible.biblestudy.dailybible.utils.DateTimeUtil
import com.holybible.biblestudy.dailybible.view.ui.calendar.EventDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@SuppressLint("UseCompatLoadingForDrawables")
open class CalenderFragment : Fragment() {
    private lateinit var locale: Locale
    private lateinit var binding: FragmentCalendarBinding
    private var selectedDay: CalendarDay? = null
    private val eventDays = mutableListOf<CalendarDay>()
    private lateinit var diaryAdapter: DiaryCalenderAdapter
    private lateinit var diaryDao: DiaryDao
    private lateinit var diaryViewModel: DiaryViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        try {
            initView()
            viewListener()
            diaryDao = getDatabase(requireActivity()).diaryDao()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return binding.root
    }

    private fun initView() {
        LogEven.logEvent(requireActivity(), "calender_view", Bundle())
        SystemUtil.setLocale(requireActivity())
        if (SharePrefUtils.getTheme(requireActivity()) == 2 || SharePrefUtils.getTheme(
                requireActivity()
            ) == 5
        ) {
            binding.tvHeader.setTextColor(requireActivity().getColor(R.color.color_F4F5F5))
        } else {
            binding.tvHeader.setTextColor(requireActivity().getColor(R.color.color_292B2B))
        }
        val today = CalendarDay.today()
        binding.calendarView.state().edit().setMaximumDate(today).commit()
        locale = Locale(SystemUtil.getPreLanguage(requireActivity()))
        diaryViewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]
        binding.calendarView.setWeekDayTextAppearance(R.style.weekText)
        binding.calendarView.setDateTextAppearance(R.style.dateText)
        binding.calendarView.showOtherDates = MaterialCalendarView.SHOW_NONE
        updateDecorators()
        val simpleDateFormat2 =
            SimpleDateFormat("dd/MM/yyyy", Locale(SystemUtil.getPreLanguage(requireActivity())))
        val simpleDateFormat =
            SimpleDateFormat("MMMM, yyyy", Locale(SystemUtil.getPreLanguage(requireActivity())))
        binding.tvMonth.text = simpleDateFormat.format(Date())
        binding.calendarView.setOnMonthChangedListener { _, calendarDay ->
            val year = calendarDay.year
            val month = calendarDay.month
            val day = calendarDay.day
            var month1 = "" + month
            if (month < 10) month1 = "0$month"
            var day1 = "" + day
            if (month < 10) day1 = "0$day"
            var date = "$day1/$month1/$year"
            try {
                val date1: Date = simpleDateFormat2.parse(date)!!
                date = simpleDateFormat.format(date1)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            binding.tvMonth.text = date

        }

        binding.calendarView.setOnDateChangedListener { _, calendarDay, _ ->
            LogEven.logEvent(requireActivity(), "date_click", Bundle())
            selectedDay = calendarDay
            updateDecorators()
            selectDay(calendarDayToMilliseconds(selectedDay!!))

        }

        binding.calendarView.setTitleFormatter { "" }
        setEventCurrentDate()


        diaryViewModel.allCVs.observe(viewLifecycleOwner) { diary ->

            setEvent(convertToDayModelList(diary))
            updateDecorators()
        }

        diaryAdapter = DiaryCalenderAdapter(requireActivity()) { id ->
            showLoading()
            if (SharePrefRemote.get_config(
                    requireActivity(), SharePrefRemote.inter_preview
                ) && InterAdHelper.canShowNextAd(requireActivity()) && AdsConsentManager.getConsentResult(requireActivity())
            ) {
                InterAdHelper.showListInterAd(
                    requireActivity(), SharePrefRemote.get_config(
                        requireActivity(), SharePrefRemote.inter_preview
                    ), AdmobApi.getInstance().getListIDByName("inter_create")
                ) {
                    val intent = Intent(requireActivity(), CreateGetActivity::class.java).apply {
                        putExtra("IDDIARY", id)
                    }
                    startActivity(intent)
                    hideLoading()
                }
            }else{
                val intent = Intent(requireActivity(), CreateGetActivity::class.java).apply {
                    putExtra("IDDIARY", id)
                }
                startActivity(intent)
                hideLoading()
            }

        }
        binding.rcv.adapter = diaryAdapter
        binding.rcv.layoutManager = LinearLayoutManager(requireActivity())


    }

    private fun updateDecorators() {
        binding.calendarView.removeDecorators()
        addEventDaysDecorators()
        selectedDay?.let {
            if (eventDays.contains(it)) {
                binding.calendarView.addDecorator(
                    SelectedDayDecorator(
                        requireActivity(), it, R.drawable.ic_cell_day_pray_v3
                    )
                )
            } else {
                binding.calendarView.addDecorator(
                    SelectedDayDecorator(
                        requireActivity(), it, R.drawable.ic_cell_day_pray_v2
                    )
                )
            }

        }
    }

    private fun viewListener() {
        binding.nextMonth.setOnClickListener { binding.calendarView.goToNext() }
        binding.preMonth.setOnClickListener { binding.calendarView.goToPrevious() }
    }

    private fun addEventDaysDecorators() {
        if (eventDays.isNotEmpty()) {
            binding.calendarView.addDecorator(
                EventDecorator(
                    requireActivity(), R.drawable.ic_cell_day_pray, eventDays.toHashSet(), "#373A3A"
                )
            )
            Log.d("TAG", "viewListener:${eventDays.size}")
        }

    }

    private fun setEvent(dates: List<DayModel>) {
        eventDays.clear()
        dates.forEach { date ->
            val dateValue = DateTimeUtil.formatDatePray(date.datePray, requireActivity())
            if (dateValue.isNotEmpty()) {
                val calendarDay = CalendarDay.from(dateValue[0], dateValue[1], dateValue[2])
                eventDays.add(calendarDay)
            }
        }
        addEventDaysDecorators()
    }

    private fun setEventCurrentDate() {
        val format =
            SimpleDateFormat("dd-MM-yyyy", Locale(SystemUtil.getPreLanguage(requireActivity())))
        val currentDate = format.format(Date())
        val dateValue = DateTimeUtil.formatDatePray(currentDate, requireActivity())

        val datesIndependent: MutableList<CalendarDay> = ArrayList()
        datesIndependent.add(CalendarDay.from(dateValue[0], dateValue[1], dateValue[2]))
        setDecor(datesIndependent, R.drawable.ic_cell_day_pray_v2, "#00ffffff", 1)
    }

    private fun setDecor(
        calendarDayList: List<CalendarDay>, drawable: Int, textColor: String, type: Int? = null
    ) {
        binding.calendarView.addDecorators(
            EventDecorator(
                requireActivity(), drawable, calendarDayList.toHashSet(), textColor, type
            )
        )
    }

    class SelectedDayDecorator(
        private val context: Context,
        private val selectedDate: CalendarDay,
        private val drawableRes: Int
    ) : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return day == selectedDate
        }

        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(context.getDrawable(drawableRes)!!)
        }
    }

    private fun selectDay(time: Long) {
        lifecycleScope.launch {
            val list = diaryDao.getListByTimeInMillis(time)
            withContext(Dispatchers.Main) {
                if (list.isNotEmpty()) {
                    diaryAdapter.updateList(list)
                    binding.rcv.visible()
                    binding.rLEmpty.gone()
                } else {
                    diaryAdapter.updateList(emptyList())
                    binding.rcv.gone()
                    binding.rLEmpty.visible()
                }
            }
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = time

        }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayWeekString = when (dayOfWeek) {
            Calendar.SUNDAY -> getString(R.string.day_sun)
            Calendar.MONDAY -> getString(R.string.day_mon)
            Calendar.TUESDAY -> getString(R.string.day_tue)
            Calendar.WEDNESDAY -> getString(R.string.day_wed)
            Calendar.THURSDAY -> getString(R.string.day_Thu)
            Calendar.FRIDAY -> getString(R.string.day_fri)
            Calendar.SATURDAY -> getString(R.string.day_sat)
            else -> ""
        }
        val monthName = SimpleDateFormat("MMM ", locale).format(calendar.time)
        binding.txtDay.text = day.toString()

        binding.txtRank.text = "${dayWeekString}, "
        binding.txtMonth.text = monthName
    }

    private fun calendarDayToMilliseconds(calendarDay: CalendarDay): Long {
        return calendarDay.date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    private fun convertToDayModelList(diaryList: List<DiaryTable>): List<DayModel> {
        return diaryList.map { diary ->
            val dateString = longToDateString(diary.timeInMillis)
            Log.d("formatDatePray", diary.timeInMillis.toString())
            DayModel(id = diary.id.toLong(), datePray = dateString)
        }
    }

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            SystemUtil.setLocale(requireActivity())
        }
        super.onResume()
        if (selectedDay != null) {
            selectDay(calendarDayToMilliseconds(selectedDay!!))
        }
    }

    private fun longToDateString(timeInMillis: Long): String {
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return formatter.format(Date(timeInMillis))
    }
    private var backgroundView: FrameLayout? = null
    private var loadingLayout: View? = null

    @SuppressLint("InflateParams")
    protected open fun showLoading() {
        if (loadingLayout == null) {
            val li = LayoutInflater.from(requireActivity())
            loadingLayout = li.inflate(R.layout.layout_loading_progress, null, false)
            backgroundView = loadingLayout!!.findViewById(R.id.root)
            val rootView = requireActivity().findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(
                loadingLayout,
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            backgroundView!!.isClickable = true
        }
    }

    protected open fun hideLoading() {
        if (loadingLayout != null) {
            val rootView = requireActivity().findViewById<ViewGroup>(android.R.id.content)
            rootView.removeView(loadingLayout)
            if (backgroundView != null) backgroundView!!.isClickable = false
            loadingLayout = null
        }
    }
}
