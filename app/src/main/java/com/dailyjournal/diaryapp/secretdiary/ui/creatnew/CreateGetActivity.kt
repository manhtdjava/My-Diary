package com.dailyjournal.diaryapp.secretdiary.ui.creatnew

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.BannerGravity
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity
import com.dailyjournal.diaryapp.secretdiary.data.DiaryDatabase.Companion.getDatabase
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityGetNewBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogPopupEditBinding
import com.dailyjournal.diaryapp.secretdiary.dialog.DialogDelete
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel
import com.dailyjournal.diaryapp.secretdiary.model.HastagModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel
import com.dailyjournal.diaryapp.secretdiary.model.SoundsModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.BackGroundCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.ColorTextCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.EmojiCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.FontTextCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.HastagAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.PhotosAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.RecordCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.`object`.ItemsProvider
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.viewmodel.DiaryViewModel
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateGetActivity : BaseActivity<ActivityGetNewBinding>() {
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var locale: Locale
    private lateinit var adapterPhoto: PhotosAdapter
    private var diaryId: Int = -1
    private lateinit var diaryTable: DiaryTable
    private lateinit var adapterBackGround: BackGroundCreatNewAdapter
    private lateinit var adapterEmoji: EmojiCreatNewAdapter
    private lateinit var adapterFontColor: FontTextCreatNewAdapter
    private val soundsList = mutableListOf<SoundsModel>()
    private val hashtagList = mutableListOf<HastagModel>()
    private val photoList = mutableListOf<PhotosModel>()
    private lateinit var adapterTextColor: ColorTextCreatNewAdapter
    private lateinit var adapterHastag: HastagAdapter
    private lateinit var adapterRecord: RecordCreatNewAdapter
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1
    private var isPaused: Boolean = false
    private var savedSelectedPositionBackGround: Int = -1
    private var savedSelectedPositionColor: Int = -1
    private var savedSelectedPositionFont: Int = -1
    private var savedSelectedPositionEmoji: Int = -1
    private var countdownTimer: CountDownTimer? = null

    override fun setViewBinding(): ActivityGetNewBinding {
        return ActivityGetNewBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        LogEven.logEvent(this, "detail_view", Bundle())
        binding.edtTilte.isFocusable = false
        binding.edtTilte.isCursorVisible = false
        binding.edtContent.isFocusable = false
        binding.edtContent.isCursorVisible = false
        diaryId = intent.getIntExtra("IDDIARY", -1)
        locale = Locale(SystemUtil.getPreLanguage(this))
        val colorItems = ItemsProvider.getDefaultColors(null)
        adapterTextColor = ColorTextCreatNewAdapter(this, colorItems) { color ->
            Log.d("SelectedColor", "Color selected: $color")
        }
        val items = listOf(
            FontTextModel(0, "lowanoldstown_roman", R.drawable.ic_lowan_old_style),
            FontTextModel(1, "lemonada_light", R.drawable.ic_lemonada),
            FontTextModel(2, "marckscript_regular", R.drawable.ic_marck_script),
            FontTextModel(3, "plus_jakarta_sans_400", R.drawable.ic_plus_jakrta_sans),
            FontTextModel(4, "palmcanyondrive_regular", R.drawable.ic_palm_canyon_drive),
            FontTextModel(5, "maname_regular", R.drawable.ic_maname),
        )
        adapterFontColor = FontTextCreatNewAdapter(this, items) { font ->

        }
        loadDiaryIdData(diaryId)
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
    }

    override fun viewListener() {
        setupUI(binding.ivScrollView)
        setupUI(binding.relativeLayout2)
        binding.ivBack.tap {
            finish()
        }
        binding.btnMore.tap {
            val popupBinding = DialogPopupEditBinding.inflate(layoutInflater)
            val popupWindow = PopupWindow(
                popupBinding.root,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.dimBackground.visibility = View.VISIBLE

            popupWindow.setOnDismissListener {
                binding.dimBackground.visibility = View.GONE
            }
            popupBinding.txtDelete.tap {
                LogEven.logEvent(this, "delete_diary_click", Bundle())
                val dialog = DialogDelete(this, this, {
                    deleteDiaryEntry(diaryTable)
                    popupWindow.dismiss()
                }) {
                    popupWindow.dismiss()
                }
                dialog.show()
//                AlertDialog.Builder(this@CreatGetActivity)
//                    .setTitle("Delete Diary")
//                    .setMessage("Are you sure you want to delete this diary entry?")
//                    .setPositiveButton("Delete") { _, _ ->
//                    }
//                    .setNegativeButton("Cancel", null)
//                    .show()
            }
            popupBinding.txtEdit.tap {
                LogEven.logEvent(this, "edit_diary_click", Bundle())
                showLoading()
                if (SharePrefRemote.get_config(
                        this@CreateGetActivity, SharePrefRemote.inter_create
                    ) && InterAdHelper.canShowNextAd(this) && AdsConsentManager.getConsentResult(
                        this
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        this@CreateGetActivity, SharePrefRemote.get_config(
                            this@CreateGetActivity, SharePrefRemote.inter_create
                        ), AdmobApi.getInstance().getListIDByName("inter_create")
                    ) {
                        popupWindow.dismiss()
                        val inten = Intent(this, CreateEditActivity::class.java).apply {
                            putExtra("IDDIARY", diaryId)
                            putExtra("SELECTEDCOLOR", savedSelectedPositionColor)
                            putExtra("SELECTEDFONT", savedSelectedPositionFont)
                            putExtra("SELECTEDBACK", savedSelectedPositionBackGround)
                        }
                        startActivity(inten)
                        finish()
                        hideLoading()
                    }
                } else {
                    popupWindow.dismiss()
                    val inten = Intent(this, CreateEditActivity::class.java).apply {
                        putExtra("IDDIARY", diaryId)
                        putExtra("SELECTEDCOLOR", savedSelectedPositionColor)
                        putExtra("SELECTEDFONT", savedSelectedPositionFont)
                        putExtra("SELECTEDBACK", savedSelectedPositionBackGround)
                    }
                    startActivity(inten)
                    finish()
                    hideLoading()
                }

            }
            val location = IntArray(2)
            binding.btnMore.getLocationOnScreen(location)

            popupWindow.showAtLocation(
                binding.root,
                Gravity.NO_GRAVITY,
                location[0] + binding.btnMore.width,
                location[1] - popupBinding.root.measuredHeight
            )
        }
    }

    private fun loadDiaryIdData(diaryId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val diaryDao = getDatabase(this@CreateGetActivity).diaryDao()
                diaryTable = diaryDao.getPFById(diaryId) ?: return@launch
                Log.d("pfTable", diaryTable.toString())

                withContext(Dispatchers.Main) {
                    // Log.d("CheckInput", "Font: ${diaryTable.fontTypeface}, Color: ${diaryTable.textColor}")
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = diaryTable.timeInMillis
                    }
                    updateDateViews(calendar)

                    val emojis = ItemsProvider.getDefaultEmoji()
                    val selectedEmoji = emojis.find { it.id == diaryTable.statusEmoji }?.image
                    if (selectedEmoji != null) {
                        binding.ivEmoji.setImageResource(selectedEmoji)
                    } else {
                        binding.ivEmoji.setImageResource(R.drawable.ic_emotion)
                    }

                    if (diaryTable.title.isNotEmpty()) {
                        binding.edtTilte.setText(diaryTable.title)
                    } else {
                        binding.edtTilte.visibility = View.GONE
                    }
                    if (diaryTable.content.isNotEmpty()) {
                        binding.edtContent.setText(diaryTable.content)
                    } else {
                        binding.edtContent.visibility = View.GONE
                    }


                    binding.rcvListHastag.layoutManager =
                        FlexboxLayoutManager(this@CreateGetActivity)
                    adapterHastag = HastagAdapter(
                        this@CreateGetActivity,
                        hashtagList,
                        diaryTable.fontTypeface,
                        diaryTable.textColor,
                        diaryTable.textSize,
                        diaryTable.textStyle
                    ) { position ->

                    }
                    adapterHastag.setMode("getTable")
                    binding.rcvListHastag.adapter = adapterHastag
                    hashtagList.addAll(diaryTable.hastagList)
                    adapterHastag.updateList(
                        diaryTable.hastagList,
                        diaryTable.textColor,
                        diaryTable.fontTypeface,
                        diaryTable.textSize,
                        diaryTable.textStyle
                    )

                    binding.rcvListRecord.layoutManager =
                        LinearLayoutManager(this@CreateGetActivity)
                    adapterRecord = RecordCreatNewAdapter(this@CreateGetActivity, { _ ->
                    }, { position ->
                        playSound(position)
                    }, { _ ->
                        stopCurrentSound()
                    }, { _, progress ->
                        mediaPlayer?.seekTo(progress)
                    })
                    adapterRecord.setMode("getTable")
                    binding.rcvListRecord.adapter = adapterRecord
                    val validSounds = diaryTable.listSounds.filter { sound ->
                        val file = File(sound.filePath)
                        file.exists()
                    }
                    soundsList.clear()
                    soundsList.addAll(validSounds)
                    adapterRecord.setData(validSounds)

                    binding.rcvListImage.layoutManager = LinearLayoutManager(this@CreateGetActivity)
                    adapterPhoto = PhotosAdapter(
                        this@CreateGetActivity,
                        diaryTable.fontTypeface,
                        diaryTable.textColor,
                        diaryTable.textDots,
                        diaryTable.alignment,
                        diaryTable.textSize,
                        diaryTable.textStyle
                    ) { uri ->
                        Log.d("Selected Image URI", "Selected URI: $uri")
                    }
                    adapterPhoto.setMode("getTable")
                    binding.rcvListImage.adapter = adapterPhoto
                    photoList.addAll(diaryTable.listPhoto)
                    adapterPhoto.updateList(diaryTable.listPhoto)
                    savedSelectedPositionBackGround = diaryTable.backGround
                    if (diaryTable.backGroundUri?.let { Uri.parse(it).isAbsolute } == true) {
                        Log.d("activityCreat", diaryTable.backGroundUri)
                        Glide.with(this@CreateGetActivity).load(diaryTable.backGroundUri)
                            .into(object : CustomTarget<Drawable>() {
                                override fun onResourceReady(
                                    resource: Drawable, transition: Transition<in Drawable>?
                                ) {
                                    binding.activityCreat.background = resource
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })
                    } else {
                        Log.d("pfTable", diaryTable.backGround.toString())
                        val backgrounds = ItemsProvider.getDefaultBack()
                        val selectedBackground = backgrounds.filter { it.id != 0 }
                            .find { it.id == diaryTable.backGround }?.image
                        if (selectedBackground != null) {
                            binding.activityCreat.background = getDrawable(selectedBackground)
                        } else {
                            binding.activityCreat.background = getDrawable(R.color.color_white)
                        }
                    }

                    when (diaryTable.alignment) {
                        1 -> changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_VIEW_START
                        )

                        2 -> changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_CENTER
                        )

                        3 -> changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_VIEW_END
                        )

                        else -> changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_VIEW_START
                        )
                    }

                    when (diaryTable.textStyle) {
                        1 -> changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.BOLD
                        )

                        2 -> changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.ITALIC
                        )

                        3 -> changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.NORMAL
                        )

                        else -> changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.NORMAL
                        )
                    }

                    when (diaryTable.textSize) {
                        1 -> changeTextSizeInLayout(binding.ivContent as ViewGroup, 0)
                        2 -> changeTextSizeInLayout(binding.ivContent as ViewGroup, 3)
                        3 -> changeTextSizeInLayout(binding.ivContent as ViewGroup, 6)
                        else -> changeTextSizeInLayout(binding.ivContent as ViewGroup, 0)
                    }

                    if (diaryTable.textDots == 1) {
                        addPrefixToViews(binding.ivContent as ViewGroup)
                    } else {
                        clearPrefixToViews(binding.ivContent as ViewGroup)
                    }

                    adapterFontColor.setSelectedFont(diaryTable.fontTypeface)
                    savedSelectedPositionFont = adapterFontColor.getSelectedPosition()
//                    changeTextFontInLayout(binding.ivContent, diaryTable.fontTypeface)
//                    changeTextFontInLayout(binding.ivDateTime, diaryTable.fontTypeface)
                    checkFont2(diaryTable.fontTypeface)

                    adapterTextColor.setSelectedColor(diaryTable.textColor)
                    savedSelectedPositionColor = adapterTextColor.getSelectedPosition()
                    changeTextColorInLayout(binding.ivContent as ViewGroup, diaryTable.textColor)
                    changeTextColorInLayout(binding.ivLldate as ViewGroup, diaryTable.textColor)

                }
            } catch (e: Exception) {
                Log.d("loadDiaryIdData", "Error loading diary data: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun deleteDiaryEntry(diaryId: DiaryTable) {
        lifecycleScope.launch {
            try {
                diaryViewModel.deletePF(diaryId)
                Toast.makeText(
                    this@CreateGetActivity, R.string.diary_entry_saved, Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@CreateGetActivity, R.string.failed_to_save_diary_entry, Toast.LENGTH_SHORT
                ).show()
                Log.e("deleteDiaryEntry", "Error deleting diary entry: ${e.message}")
            }
        }
    }

    private fun setupUI(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

    private fun hideKeyboard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = this.currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun playSound(position: Int) {
        val sounda = soundsList[position]
        val filePath = sounda.filePath
        val file = File(filePath)

        if (!file.exists()) {
            Log.e("playSound", "File not found: $filePath")
            Toast.makeText(this, R.string.audio_file_not_found, Toast.LENGTH_SHORT).show()
            return
        }

        if (currentPlayingPosition == position) {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                isPaused = true
                soundsList[position].isPaused = true
            } else {
                mediaPlayer?.start()
                isPaused = false
                soundsList[position].isPaused = false
            }
            adapterRecord.notifyItemChanged(position)
            return
        }
        stopCurrentSound()
        for (i in soundsList.indices) {
            if (i != position) {
                soundsList[i].progress = 0
                soundsList[i].isPlay = false
                soundsList[i].isPaused = false
                adapterRecord.notifyItemChanged(i)
            }
        }
        val sound = soundsList[position]
        mediaPlayer = MediaPlayer().apply {
            setDataSource(sound.filePath)
            prepare()
            seekTo(sound.progress.toInt())
            start()
            currentPlayingPosition = position
            isPaused = false
            sound.isPaused = false
            sound.isPlay = true

            setOnCompletionListener {
                stopCurrentSound()
                currentPlayingPosition = -1
                sound.progress = 0
                sound.isPlay = false
                adapterRecord.notifyItemChanged(position)
            }
        }
        updateSeekBar(position)
        adapterRecord.notifyItemChanged(position)
    }

    private fun stopCurrentSound() {
        if (currentPlayingPosition >= 0 && currentPlayingPosition < soundsList.size) {
            val sound = soundsList[currentPlayingPosition]

            val currentPosition = mediaPlayer?.currentPosition ?: 0
            sound.progress = currentPosition

            mediaPlayer?.apply {
                stop()
                reset()
                release()
            }
            mediaPlayer = null

            currentPlayingPosition = -1
            isPaused = false
            countdownTimer?.cancel()
            sound.isPlay = false
            sound.isPaused = false
            adapterRecord.notifyItemChanged(currentPlayingPosition)
        }
    }

    private fun updateSeekBar(position: Int) {
        val sound = soundsList[position]
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(sound.duration, 1200) {
            override fun onTick(millisUntilFinished: Long) {
                mediaPlayer?.let {
                    val elapsedTime = it.currentPosition
                    sound.progress = elapsedTime
                    adapterRecord.updateSeekBar(position, elapsedTime, it.duration)
                }
            }

            override fun onFinish() {
                stopCurrentSound()
                isPaused = false
                sound.isPaused = false
                sound.progress = 0
                sound.isPlay = false
                adapterRecord.notifyItemChanged(position)
            }
        }
        countdownTimer?.start()
    }


    private fun updateDateViews(calendar: Calendar) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekString = when (dayOfWeek) {
            Calendar.SUNDAY -> getString(R.string.day_sun)
            Calendar.MONDAY -> getString(R.string.day_mon)
            Calendar.TUESDAY -> getString(R.string.day_tue)
            Calendar.WEDNESDAY -> getString(R.string.day_wed)
            Calendar.THURSDAY -> getString(R.string.day_Thu)
            Calendar.FRIDAY -> getString(R.string.day_fri)
            Calendar.SATURDAY -> getString(R.string.day_sat)
            else -> ""
        }
        val monthName = SimpleDateFormat("MMM  yyyy", locale).format(calendar.time)
        binding.txtDay.text = day.toString()
        binding.txtRank.text = dayOfWeekString
        binding.txtMonth.text = monthName
    }


    private fun changeTextAlignmentInLayout(viewGroup: ViewGroup, alignment: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                child.textAlignment = alignment
                when (alignment) {
                    View.TEXT_ALIGNMENT_VIEW_START -> child.gravity = Gravity.START
                    View.TEXT_ALIGNMENT_CENTER -> child.gravity = Gravity.CENTER
                    View.TEXT_ALIGNMENT_VIEW_END -> child.gravity = Gravity.END
                }
                child.invalidate()
                child.requestLayout()
            } else if (child is TextView) {
                child.textAlignment = alignment
                when (alignment) {
                    View.TEXT_ALIGNMENT_VIEW_START -> child.gravity = Gravity.START
                    View.TEXT_ALIGNMENT_CENTER -> child.gravity = Gravity.CENTER
                    View.TEXT_ALIGNMENT_VIEW_END -> child.gravity = Gravity.END
                }
                child.invalidate()
                child.requestLayout()
            } else if (child is ViewGroup) {
                changeTextAlignmentInLayout(child, alignment)
            }
        }
        viewGroup.invalidate()
    }


    private fun changeTextStyleInLayout(viewGroup: ViewGroup, style: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                child.setTypeface(null, style)
                child.invalidate()
                child.requestLayout()
            } else if (child is TextView) {
                child.setTypeface(null, style)
                child.invalidate()
                child.requestLayout()
            } else if (child is ViewGroup) {
                changeTextStyleInLayout(child, style)
            }
        }
        viewGroup.invalidate()
    }

    private fun addPrefixToViews(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                val drawable = ContextCompat.getDrawable(this, R.drawable.ic_dot_image)
                drawable?.let {
                    child.setCompoundDrawablesWithIntrinsicBounds(it, null, null, null)
                    child.compoundDrawablePadding = 16
                }
            } else if (child is ViewGroup) {
                addPrefixToViews(child)
            }
        }
    }

    private fun clearPrefixToViews(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                child.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                child.compoundDrawablePadding = 0
            } else if (child is ViewGroup) {
                addPrefixToViews(child)
            }
        }
    }

    private fun changeTextColorInLayout(viewGroup: ViewGroup, textColor: String) {
        if (textColor.isNullOrEmpty()) {
            Log.d("ChangeTextColor", "Text color is null or empty, skipping color change.")
            return
        }
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                child.setTextColor(Color.parseColor(textColor))
                //child.setHintTextColor(Color.parseColor(textColor))
                child.invalidate()
                child.requestLayout()
            } else if (child is TextView) {
                child.setTextColor(Color.parseColor(textColor))
                child.invalidate()
                child.requestLayout()
            } else if (child is ViewGroup) {
                changeTextColorInLayout(child, textColor)
            }
        }
        viewGroup.invalidate()
    }

    private fun checkFont2(font: String) {
        when (font) {
            "lowanoldstown_roman" -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "lowan_old_style_700",
                        binding.edtContent to "lowanoldstown_roman"
                    )
                )
                changeTextFontInLayout2(binding.ivContent, font)
            }

            "lemonada_light" -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "lemonada_bold_700",
                        binding.edtContent to "lemonada_light"
                    )
                )
                changeTextFontInLayout2(binding.ivContent, font)
            }

            "plus_jakarta_sans_400" -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "plus_jakarta_sans_700",
                        binding.edtContent to "plus_jakarta_sans_400"
                    )
                )
                changeTextFontInLayout2(binding.ivContent, font)
            }

            else -> {
                changeTextFontInLayout2(binding.ivContent, font)
                //  changeTextFontInLayout2(binding.ivDateTime, font)
            }
        }
    }

    private fun applyCustomFonts(viewFontMap: Map<View, String>) {
        viewFontMap.forEach { (view, fontName) ->
            val fontId = resources.getIdentifier(fontName, "font", packageName)
            if (fontId != 0) {
                val customFont = ResourcesCompat.getFont(this, fontId)
                if (customFont != null) {
                    val currentStyle = when (view) {
                        is TextView -> view.typeface?.style ?: Typeface.NORMAL
                        is EditText -> view.typeface?.style ?: Typeface.NORMAL
                        else -> Typeface.NORMAL
                    }
                    val updatedFont = Typeface.create(customFont, currentStyle)
                    when (view) {
                        is EditText -> {
                            view.typeface = updatedFont
                            view.invalidate()
                            view.requestLayout()
                        }

                        is TextView -> {
                            view.typeface = updatedFont
                            view.invalidate()
                            view.requestLayout()
                        }
                    }
                }
            }
            view.invalidate()
            view.requestLayout()
        }
    }

    private fun changeTextFontInLayout2(viewGroup: ViewGroup, fontTypeface: String) {
        if (fontTypeface.isNullOrEmpty()) {
            Log.d("ChangeTextFont", "Font typeface is null or empty, skipping font change.")
            return
        }
        try {
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                if (child is EditText) {
                    val fontId = resources.getIdentifier(fontTypeface, "font", packageName)
                    if (fontId == 0) {
                        Log.e("ChangeTextFont", "Invalid font resource ID for: $fontTypeface")
                        return
                    }
                    val customFont = ResourcesCompat.getFont(this, fontId)
                    //hild.typeface = customFont
                    val currentStyle = child.typeface?.style ?: Typeface.NORMAL
                    if (customFont != null) {
                        child.typeface = Typeface.create(customFont, currentStyle)
                    }
                    Log.d("FontChange", "Font applied to: $customFont")
                    child.invalidate()
                    child.requestLayout()
                } else if (child is TextView) {
                    val fontId = resources.getIdentifier(fontTypeface, "font", packageName)
                    if (fontId == 0) {
                        Log.e("ChangeTextFont", "Invalid font resource ID for: $fontTypeface")
                        return
                    }
                    val customFont = ResourcesCompat.getFont(this, fontId)
                    val currentStyle = child.typeface?.style ?: Typeface.NORMAL
                    if (customFont != null) {
                        child.typeface = Typeface.create(customFont, currentStyle)
                    }
                    child.invalidate()
                    child.requestLayout()
                } else if (child is ViewGroup) {
                    changeTextFontInLayout2(child, fontTypeface)
                }
            }
            viewGroup.invalidate()
            viewGroup.requestLayout()
        } catch (e: IllegalArgumentException) {
            Log.e("ChangeTextColor", "Invalid text color: $fontTypeface")
        }
    }

    private fun changeTextSizeInLayout(viewGroup: ViewGroup, size: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                if (child.tag == "title") {
                    val currentSize = 24
                    val newSize = currentSize + size
                    child.textSize = newSize.toFloat()
                    child.invalidate()
                    child.requestLayout()
                } else {
                    val currentSize = 16
                    val newSize = currentSize + size
                    child.textSize = newSize.toFloat()
                    child.invalidate()
                    child.requestLayout()
                }
            } else if (child is TextView) {
                if (child.tag == "title") {
                    val currentSize = 24
                    val newSize = currentSize + size
                    child.textSize = newSize.toFloat()
                    child.invalidate()
                    child.requestLayout()
                } else {
                    val currentSize = 16
                    val newSize = currentSize + size
                    child.textSize = newSize.toFloat()
                    child.invalidate()
                    child.requestLayout()
                }
            } else if (child is ViewGroup) {
                changeTextSizeInLayout(child, size)
            }
        }
        viewGroup.invalidate()
    }

    override fun dataObservable() {
    }

    override fun onStop() {
        super.onStop()
        countdownTimer?.cancel()
        countdownTimer = null
        mediaPlayer?.stop()

        mediaPlayer?.release()
    }

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    private fun loadNative() {
        try {
            val showNative =
                SharePrefRemote.get_config(this, SharePrefRemote.native_view_edit)
            val showCollapse =
                SharePrefRemote.get_config(this, SharePrefRemote.collapse_banner_edit)
            val hasConsent = AdsConsentManager.getConsentResult(this)

            if (hasConsent) {
                when {
                    showNative && !showCollapse-> {
                        binding.frAds.visibility = View.VISIBLE
                        binding.include.visibility = View.GONE

                        val nativeBuilder = NativeBuilder(
                            this,
                            binding.frAds,
                            R.layout.ads_native_small_top_shimer,
                            R.layout.layout_native_small_top,
                            R.layout.layout_native_small_top
                        )
                        nativeBuilder.setListIdAd(
                            AdmobApi.getInstance().getListIDByName("native_home")
                        )

                        NativeManager(this, this, nativeBuilder)
                            .setIntervalReloadNative(Contants.interval_reload_native)
                    }

                    showCollapse -> {
                        binding.frAds.visibility = View.GONE
                        runnable = Runnable {
                            Admob.getInstance().loadCollapsibleBannerFloor(
                                this,
                                AdmobApi.getInstance().getListIDByName("collapse_banner_home"),
                                BannerGravity.bottom
                            )
                            binding.include.visibility = View.VISIBLE
                            handler.postDelayed(runnable, Contants.collap_reload_interval)
                        }
                        handler.post(runnable)
                    }

                    else -> {
                        binding.frAds.visibility = View.GONE
                        binding.frAds.removeAllViews()
                        binding.include.visibility = View.GONE
                    }
                }
            } else {
                binding.frAds.visibility = View.GONE
                binding.frAds.removeAllViews()
                binding.include.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
            binding.include.visibility = View.GONE
            handler.removeCallbacks(runnable)
        }
    }


//    private fun loadNative() {
//        try {
//            if (SharePrefRemote.get_config(
//                    this, SharePrefRemote.native_view_edit
//                ) && AdsConsentManager.getConsentResult(this)
//            ) {
//
//                binding.frAds.visibility = View.VISIBLE
//                val nativeBuilder = NativeBuilder(
//                    this,
//                    binding.frAds,
//                    R.layout.ads_native_small_top_shimer,
//                    R.layout.layout_native_small_top,
//                    R.layout.layout_native_small_top
//                )
//                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_view_edit"))
//                val nativeManager = NativeManager(
//                    this, this, nativeBuilder
//                ).setIntervalReloadNative(Contants.interval_reload_native)
//            } else {
//                binding.frAds.visibility = View.GONE
//                binding.frAds.removeAllViews()
//            }
//        } catch (exception: Exception) {
//            exception.printStackTrace()
//            binding.frAds.visibility = View.GONE
//            binding.frAds.removeAllViews()
//        }
//    }
}






