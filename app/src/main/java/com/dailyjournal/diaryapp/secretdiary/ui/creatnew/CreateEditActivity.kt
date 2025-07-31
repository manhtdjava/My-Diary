package com.dailyjournal.diaryapp.secretdiary.ui.creatnew

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazic.ads.callback.RewardCallback
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.amazic.ads.util.BannerGravity
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseActivity2
import com.dailyjournal.diaryapp.secretdiary.data.DiaryDatabase.Companion.getDatabase
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityCreatNewBinding
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogSoundStartCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.dialog.DialogSave
import com.dailyjournal.diaryapp.secretdiary.dialog.RecordingDialog
import com.dailyjournal.diaryapp.secretdiary.listener.SaveRecorderListener
import com.dailyjournal.diaryapp.secretdiary.model.BackGroundModel
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel
import com.dailyjournal.diaryapp.secretdiary.model.HastagModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel
import com.dailyjournal.diaryapp.secretdiary.model.SoundsModel
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.BackGroundCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.ColorTextCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.EmojiCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.FontTextCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.HastagAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.PhotosAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.RecordCreatNewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.colorpicker.ColorPickerPopUp
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.TedImagePicker
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.`object`.ItemsProvider
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default.CAMERA_PERMISSION
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default.RECORD_PERMISSION
import com.dailyjournal.diaryapp.secretdiary.viewmodel.DiaryViewModel
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.dailyjournal.diaryapp.secretdiary.widget.visible
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateEditActivity : BaseActivity2<ActivityCreatNewBinding>(), SaveRecorderListener {
    private lateinit var locale: Locale
    private var selectedUriList: List<Uri>? = null
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryTable: DiaryTable
    private lateinit var adapterPhoto: PhotosAdapter
    private lateinit var adapterBackGround: BackGroundCreatNewAdapter
    private lateinit var adapterEmoji: EmojiCreatNewAdapter
    private lateinit var adapterHastag: HastagAdapter
    private val hashtagList = mutableListOf<HastagModel>()
    private lateinit var adapterTextColor: ColorTextCreatNewAdapter
    private lateinit var adapterFontColor: FontTextCreatNewAdapter
    private lateinit var colorPickerPopUp: ColorPickerPopUp
    private val soundsList = mutableListOf<SoundsModel>()
    private lateinit var adapterRecord: RecordCreatNewAdapter
    private val PICK_AUDIO_REQUEST_CODE = 1001
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1
    private var isPaused: Boolean = false
    private var countdownTimer: CountDownTimer? = null
    private var newColor: String? = null ?: "#FF7375"
    private var statusEmoji: Int? = null
    private var selectedFont: String? = null
    private var background: Int? = null
    private var alignmentInt: Int? = null
    private var textStyleInt: Int? = null
    private var textDotsInt: Int? = null
    private var backgroundUri: String? = null
    private var textColorInt: String? = null
    private var textSizeInt: Int? = null
    private var selectedTimeInMillis: Long? = null
    private var diaryId: Int = -1
    private var function = ""
    private var savedSelectedPositionBackGround: Int = -1
    private var savedSelectedPositionColor: Int = -1
    private var savedSelectedPositionFont: Int = -1
    private var savedSelectedPositionEmoji: Int = -1
    private var isBoldSelected = false
    private var isItalicSelected = false
    private var selectedCalendar: Calendar = Calendar.getInstance()
    private var backPositon: Int = -1
    private lateinit var backGroungModel: BackGroundModel
    private var funselectedBackground: Int? = null
    private var imgIcon: Int = -1
    private var updatedItems = ItemsProvider.getDefaultBack().toMutableList()
    private var isCheckCollap = false
    private var isCheckNative = false

    override fun setViewBinding(): ActivityCreatNewBinding {
        return ActivityCreatNewBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadNative()
        showUiADS()
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
        selectedCalendar.set(Calendar.MINUTE, 0)
        selectedCalendar.set(Calendar.SECOND, 0)
        selectedCalendar.set(Calendar.MILLISECOND, 0)
        binding.datePick.maxDate = System.currentTimeMillis()
        locale = Locale(SystemUtil.getPreLanguage(this))
        diaryViewModel = ViewModelProvider(this).get(DiaryViewModel::class.java)
        diaryId = intent.getIntExtra("IDDIARY", -1)
        savedSelectedPositionColor = intent.getIntExtra("SELECTEDCOLOR", -1)
        savedSelectedPositionFont = intent.getIntExtra("SELECTEDFONT", -1)
        savedSelectedPositionBackGround = intent.getIntExtra("SELECTEDBACK", -1)
        Log.d(
            "SELECTEDCOLOR",
            savedSelectedPositionColor.toString() + savedSelectedPositionFont.toString() + savedSelectedPositionBackGround
        )
        adapterColor()
        adapterFont()
        loadDiaryIdData(diaryId)
        adapterPhoto = PhotosAdapter(
            this, selectedFont, textColorInt, alignmentInt, textDotsInt, textSizeInt, textStyleInt
        ) { uri ->
            binding.rlRoomPhoto.visibility = View.VISIBLE
            Log.d("Selected Image URI", "Selected URI: $uri")
            binding.ivSetImage.scaleX = if (uri.isRotated) -1f else 1f
            Glide.with(this).load(uri.uri).placeholder(R.drawable.ic_camera_48dp)
                .error(R.drawable.bg_colorapp_radius).into(binding.ivSetImage)
        }
        binding.rcvListImage.layoutManager = LinearLayoutManager(this)
        binding.rcvListImage.adapter = adapterPhoto
        val currentDate = Calendar.getInstance()
        updateDateViews(currentDate)
        binding.rcvListRecord.layoutManager = LinearLayoutManager(this)

        adapterRecord = RecordCreatNewAdapter(this, { position ->
            if (currentPlayingPosition == position) {
                stopCurrentSound()
                currentPlayingPosition = -1
            }

            if (currentPlayingPosition > position) {
                currentPlayingPosition -= 1
            }
            soundsList.removeAt(position)
            adapterRecord.setData(soundsList)
        }, { position ->
            playSound(position)
        }, {
            stopCurrentSound()
        }, { _, progress ->
            mediaPlayer?.seekTo(progress)
        })

        binding.rcvListRecord.isNestedScrollingEnabled = false
        binding.rcvListRecord.adapter = adapterRecord
        adapterHastag = HastagAdapter(
            this, hashtagList, selectedFont, textColorInt, textSizeInt, textStyleInt
        ) { position ->
            hashtagList.removeAt(position)
            adapterHastag.updateList(
                hashtagList,
                textColorInt.toString(),
                selectedFont.toString(),
                textSizeInt ?: -1,
                textStyleInt ?: -1
            )
        }
        binding.rcvListHastag.layoutManager = FlexboxLayoutManager(this)
        binding.rcvListHastag.adapter = adapterHastag

        val items = ItemsProvider.getDefaultBack()
        adapterBackGround = BackGroundCreatNewAdapter(this, items) { backGround, position ->
            backGround(position, backGround)
            backPositon = position
            //  backGroungModel = backGround
            funselectedBackground = backGround.image
        }
        val layoutManager = GridLayoutManager(this, 4)
        // adapterBackGround.setSelectedPosition(savedSelectedPositionBackGround)
        binding.ivIncludeBackGround.rcvListBackGround.layoutManager = layoutManager
        binding.ivIncludeBackGround.rcvListBackGround.adapter = adapterBackGround
        adapterBackGround.updateList(items)

        val rootView = window.decorView.rootView

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            if (isKeyboardVisible) {
                if (isCheckCollap) {
                    binding.include.gone()
                }
                if (isCheckNative){
                    binding.frAds.gone()
                }
            } else {
                if (isCheckCollap) {
                    binding.include.visible()
                }
                if (isCheckNative){
                    binding.frAds.visible()
                }
            }
        }
    }

    override fun viewListener() {
        setupUI(binding.ivContent2)
        setupUI(binding.llRecord)
        setupUI(binding.relativeLayout2)
        binding.edtTilte.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {

                clearIconSelected()
            } else {

            }
        }
        binding.edtContent.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                clearIconSelected()
            } else {

            }
        }
        binding.btnHastag.tap {
            if (!SharePrefUtils.isCheckAdsTAG(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.rewarded_edit
                ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.show_ads
                )
            ) {
                val adIds = AdmobApi.getInstance().getListIDByName("rewarded_edit")
                if (adIds.isNotEmpty()) {
                    Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                        this@CreateEditActivity,
                        adIds.first(),
                        object : RewardCallback {
                            override fun onEarnedReward(rewardItem: RewardItem?) {
                                function = "hastag"
                                SharePrefUtils.setCheckAdsTAG(this@CreateEditActivity, true)
                                showUiADS()
                                iconSelected()
                                hastag()
                            }

                            override fun onAdClosed() {

                            }

                            override fun onAdFailedToShow(codeError: Int) {

                            }

                            override fun onAdImpression() {

                            }

                        })
                }else{
                    function = "hastag"
                    iconSelected()
                    hastag()
                }
            } else {
                function = "hastag"
                iconSelected()
                hastag()
            }
        }
        binding.btnSave.tap {
            val dialog = DialogSave(this, this, {
                saveDiaryEntry()
            }) {

            }
            dialog.show()

        }
        binding.ivBack.tap {
            val dialog = DialogSave(this, this, {
                saveDiaryEntry()
            }) {
                finish()
            }
            dialog.show()
        }
        binding.ivDateTime.tap {
            clearIconSelected()
            dateTime()
        }
        binding.ivEmoji.tap {
            if (!SharePrefUtils.isCheckAdsEMOIJ(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.rewarded_edit
                ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.show_ads
                )
            ) {
                val adIds = AdmobApi.getInstance().getListIDByName("rewarded_edit")
                if (adIds.isNotEmpty()) {
                    Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                        this@CreateEditActivity,
                        adIds.first(),
                        object : RewardCallback {
                            override fun onEarnedReward(rewardItem: RewardItem?) {
                                clearIconSelected()
                                statusEmoji()
                                SharePrefUtils.setCheckAdsEMOIJ(this@CreateEditActivity, true)
                                showUiADS()
                            }

                            override fun onAdClosed() {

                            }

                            override fun onAdFailedToShow(codeError: Int) {

                            }

                            override fun onAdImpression() {

                            }

                        })
                }else{
                    clearIconSelected()
                    statusEmoji()
                }
            } else {
                clearIconSelected()
                statusEmoji()
            }
        }
        binding.btnImages.tap {
            if (checkPermission(CAMERA_PERMISSION)) {
                if (!SharePrefUtils.isCheckAdsIMG(this) && SharePrefRemote.get_config(
                        this, SharePrefRemote.rewarded_sound_photo
                    ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                        this, SharePrefRemote.show_ads
                    )
                ) {
                    val adIds = AdmobApi.getInstance().getListIDByName("rewarded_sound_photo")
                    if (adIds.isNotEmpty()) {
                    Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                        this@CreateEditActivity,
                        adIds.first(),
                        object : RewardCallback {
                            override fun onEarnedReward(rewardItem: RewardItem?) {

                            }

                            override fun onAdClosed() {
                                SharePrefUtils.setCheckAdsIMG(this@CreateEditActivity, true)
                                function = "btnImages"
                                iconSelected()
                                TedImagePicker.with(this@CreateEditActivity)
                                    .errorListener { message -> Log.d("ted", "message: $message") }
                                    .cancelListener {
                                        Log.d("ted", "image select cancel")
                                        clearIconSelected()
                                    }
                                    //.selectedUri(selectedUriList)
                                    .startMultiImage { list: List<Uri> ->
                                        clearIconSelected()
                                        val createdAt = selectedTimeInMillis
                                            ?: Calendar.getInstance().timeInMillis
                                        // val uriStrings = list.map { it.toString() }.toMutableList()
                                        val uriStrings = list.map { uri ->
                                            PhotoItemModel(
                                                uri = uri.toString(),
                                                timeInMillis = createdAt,
                                                isRotated = false
                                            )
                                        }.toMutableList()
                                        val newPhotos = PhotosModel(uriStrings, "")
                                        adapterPhoto.addItem(
                                            newPhotos,
                                            textColorInt.toString(),
                                            selectedFont.toString(),
                                            textDotsInt ?: -1,
                                            alignmentInt ?: 1,
                                            textSizeInt ?: 1,
                                            textStyleInt ?: -1
                                        )
                                    }
                                showUiADS()
                            }

                            override fun onAdFailedToShow(codeError: Int) {

                            }

                            override fun onAdImpression() {

                            }

                        })
                        }else{
                        function = "btnImages"
                        iconSelected()
                        TedImagePicker.with(this)
                            .errorListener { message -> Log.d("ted", "message: $message") }
                            .cancelListener {
                                Log.d("ted", "image select cancel")
                                clearIconSelected()
                            }
                            //.selectedUri(selectedUriList)
                            .startMultiImage { list: List<Uri> ->
                                clearIconSelected()
                                val createdAt =
                                    selectedTimeInMillis ?: Calendar.getInstance().timeInMillis
                                // val uriStrings = list.map { it.toString() }.toMutableList()
                                val uriStrings = list.map { uri ->
                                    PhotoItemModel(
                                        uri = uri.toString(),
                                        timeInMillis = createdAt,
                                        isRotated = false
                                    )
                                }.toMutableList()
                                val newPhotos = PhotosModel(uriStrings, "")
                                adapterPhoto.addItem(
                                    newPhotos,
                                    textColorInt.toString(),
                                    selectedFont.toString(),
                                    textDotsInt ?: -1,
                                    alignmentInt ?: 1,
                                    textSizeInt ?: 1,
                                    textStyleInt ?: -1
                                )
                            }
                    }
                } else {
                    function = "btnImages"
                    iconSelected()
                    TedImagePicker.with(this)
                        .errorListener { message -> Log.d("ted", "message: $message") }
                        .cancelListener {
                            Log.d("ted", "image select cancel")
                            clearIconSelected()
                        }
                        //.selectedUri(selectedUriList)
                        .startMultiImage { list: List<Uri> ->
                            clearIconSelected()
                            val createdAt =
                                selectedTimeInMillis ?: Calendar.getInstance().timeInMillis
                            // val uriStrings = list.map { it.toString() }.toMutableList()
                            val uriStrings = list.map { uri ->
                                PhotoItemModel(
                                    uri = uri.toString(),
                                    timeInMillis = createdAt,
                                    isRotated = false
                                )
                            }.toMutableList()
                            val newPhotos = PhotosModel(uriStrings, "")
                            adapterPhoto.addItem(
                                newPhotos,
                                textColorInt.toString(),
                                selectedFont.toString(),
                                textDotsInt ?: -1,
                                alignmentInt ?: 1,
                                textSizeInt ?: 1,
                                textStyleInt ?: -1
                            )
                        }
                }

            } else {
                showDialogPermission(arrayOf(CAMERA_PERMISSION))
            }
        }

        binding.ivDeleteRoom.tap {
            binding.rlRoomPhoto.visibility = View.GONE
        }

        binding.btnBackGround.tap {
            if (checkPermission(CAMERA_PERMISSION)) {
                if (!SharePrefUtils.isCheckAdsBG(this) && SharePrefRemote.get_config(
                        this, SharePrefRemote.rewarded_edit
                    ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                        this, SharePrefRemote.show_ads
                    )
                ) {
                    val adIds = AdmobApi.getInstance().getListIDByName("rewarded_edit")
                    if (adIds.isNotEmpty()) {
                        Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                            this@CreateEditActivity,
                            adIds.first(),
                            object : RewardCallback {
                                override fun onEarnedReward(rewardItem: RewardItem?) {
                                    function = "btnBackGround"
                                    SharePrefUtils.setCheckAdsBG(this@CreateEditActivity, true)
                                    showUiADS()
                                    iconSelected()
                                    binding.ivIncludeBackGround.root.visibility = View.VISIBLE

                                }

                                override fun onAdClosed() {

                                }

                                override fun onAdFailedToShow(codeError: Int) {

                                }

                                override fun onAdImpression() {

                                }

                            })
                    }else{
                        function = "btnBackGround"
                        iconSelected()
                        binding.ivIncludeBackGround.root.visibility = View.VISIBLE
                    }
                } else {
                    function = "btnBackGround"
                    iconSelected()
                    binding.ivIncludeBackGround.root.visibility = View.VISIBLE
                }

            } else {
                showDialogPermission(arrayOf(CAMERA_PERMISSION))
            }

        }
        binding.ivIncludeBackGround.ivRest.tap {
            savedSelectedPositionBackGround = -1
            adapterBackGround.setSelectedPosition(savedSelectedPositionBackGround)

            val updatedItem = BackGroundModel(
                id = 0, image = R.drawable.bg_background_0, imageUri = null
            )

            updatedItems[0] = updatedItem
            adapterBackGround.updateList(updatedItems)
            background = null
            backgroundUri = null
            savedSelectedPositionBackGround = -1
            backPositon = -1
            binding.activityCreat.background = ContextCompat.getDrawable(this, R.color.color_white)
            binding.ivIncludeBackGround.ivRest.visibility = View.GONE
            binding.ivIncludeBackGround.ivClose.visibility = View.VISIBLE
        }
        binding.ivIncludeBackGround.ivClose.tap {
            savedSelectedPositionBackGround = -1
            adapterBackGround.setSelectedPosition(savedSelectedPositionBackGround)
            clearIconSelected()
            binding.ivIncludeBackGround.root.visibility = View.GONE
        }
        binding.btnSound.tap {
            if (checkPermission(RECORD_PERMISSION)) {
                function = "btnSound"
                iconSelected()
                binding.ivSound.root.visibility = View.VISIBLE
            } else {
                showDialogPermission(arrayOf(RECORD_PERMISSION))
            }

        }
        binding.ivSound.ivClose.tap {
            binding.ivSound.root.visibility = View.GONE
            clearIconSelected()
        }
        binding.ivSound.ivRecord.tap {
            if (!SharePrefUtils.isCheckAdsRecord(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.rewarded_sound_photo
                ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.show_ads
                )
            ) {
                val adIds = AdmobApi.getInstance().getListIDByName("rewarded_sound_photo")
                if (adIds.isNotEmpty()) {
                    Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                        this@CreateEditActivity,
                        adIds.first(),
                        object : RewardCallback {
                            override fun onEarnedReward(rewardItem: RewardItem?) {
                                SharePrefUtils.setCheckAdsRecord(this@CreateEditActivity, true)
                                recordSound()

                                showUiADS()
                            }

                            override fun onAdClosed() {

                            }

                            override fun onAdFailedToShow(codeError: Int) {

                            }

                            override fun onAdImpression() {

                            }

                        })
                }else{
                    recordSound()
                }
            } else {
                recordSound()
            }
        }
        binding.ivSound.ivMusic.tap {
            if (!SharePrefUtils.isCheckAdsMusic(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.rewarded_sound_photo
                ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.show_ads
                )
            ) {
                val adIds = AdmobApi.getInstance().getListIDByName("rewarded_sound_photo")
                if (adIds.isNotEmpty()) {
                Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                    this@CreateEditActivity,
                    adIds.first(),
                    object : RewardCallback {
                        override fun onEarnedReward(rewardItem: RewardItem?) {

                        }

                        override fun onAdClosed() {
                            SharePrefUtils.setCheckAdsMusic(this@CreateEditActivity, true)
                            pickAudioFile()

                            showUiADS()
                        }

                        override fun onAdFailedToShow(codeError: Int) {

                        }

                        override fun onAdImpression() {

                        }

                    })
                }else{
                    pickAudioFile()
                }
            } else {
                pickAudioFile()
            }

        }
        binding.btnText.tap {
            if (!SharePrefUtils.isCheckAdsTEXT(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.rewarded_edit
                ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                    this, SharePrefRemote.show_ads
                )
            ) {
                val adIds = AdmobApi.getInstance().getListIDByName("rewarded_edit")
                if (adIds.isNotEmpty()) {
                    Admob.getInstance().loadAndShowReward(this@CreateEditActivity,
                        this@CreateEditActivity,
                        adIds.first(),
                        object : RewardCallback {
                            override fun onEarnedReward(rewardItem: RewardItem?) {
                                SharePrefUtils.setCheckAdsTEXT(this@CreateEditActivity, true)
                                function = "btnText"
                                iconSelected()
                                textAlignment()
                                textStyle()
                                textDots()
                                textColor()
                                textFont()
                                textSize()
                                showUiADS()
                            }

                            override fun onAdClosed() {

                            }

                            override fun onAdFailedToShow(codeError: Int) {

                            }

                            override fun onAdImpression() {

                            }

                        })
                }else{
                    function = "btnText"
                    iconSelected()
                    textAlignment()
                    textStyle()
                    textDots()
                    textColor()
                    textFont()
                    textSize()
                }
            } else {
                function = "btnText"
                iconSelected()
                textAlignment()
                textStyle()
                textDots()
                textColor()
                textFont()
                textSize()
            }

        }
        binding.ivIncludeBackGround.ivSave.tap {
            if (backPositon == -1) {
                Toast.makeText(
                    this@CreateEditActivity,
                    R.string.you_have_not_selected_any_background_yet,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                savedSelectedPositionBackGround = backPositon
                binding.ivIncludeBackGround.ivClose.visibility = View.GONE
                binding.ivIncludeBackGround.ivRest.visibility = View.VISIBLE
                background = backPositon
                if (backPositon == 0) Glide.with(this).load(backgroundUri)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable, transition: Transition<in Drawable>?
                        ) {
                            binding.activityCreat.background = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
                else {
                    backgroundUri = null
                    binding.activityCreat.background = getDrawable(funselectedBackground!!)
                }
                clearIconSelected()
                binding.ivIncludeBackGround.root.visibility = View.GONE
            }
        }
    }

    private fun loadDiaryIdData(diaryId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        binding.ivScrollView.visibility = View.GONE
        binding.relativeLayout2.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val diaryDao = getDatabase(this@CreateEditActivity).diaryDao()
                diaryTable = diaryDao.getPFById(diaryId) ?: return@launch
                Log.d("pfTable", diaryTable.toString())
                selectedCalendar.apply {
                    timeInMillis = diaryTable.timeInMillis
                }
                selectedTimeInMillis = selectedCalendar.timeInMillis
                updateDateViews(selectedCalendar)
                statusEmoji = diaryTable.statusEmoji
                savedSelectedPositionEmoji = diaryTable.statusEmoji
                val emojis = ItemsProvider.getDefaultEmoji()
                val selectedEmoji = emojis.find { it.id == diaryTable.statusEmoji }?.image
                if (selectedEmoji != null) {
                    imgIcon = selectedEmoji
                    binding.ivEmoji.setImageResource(selectedEmoji)
                } else {
                    binding.ivEmoji.setImageResource(R.drawable.ic_emotion)
                }
                binding.edtTilte.setText(diaryTable.title)
                binding.edtContent.setText(diaryTable.content)

                binding.rcvListHastag.layoutManager = FlexboxLayoutManager(this@CreateEditActivity)
                adapterHastag = HastagAdapter(
                    this@CreateEditActivity,
                    hashtagList,
                    diaryTable.fontTypeface,
                    diaryTable.textColor,
                    diaryTable.textSize,
                    diaryTable.textStyle
                ) { position ->
                    hashtagList.removeAt(position)
                    adapterHastag.updateList(
                        hashtagList,
                        textColorInt.toString(),
                        selectedFont.toString(),
                        textSizeInt ?: -1,
                        textStyleInt ?: -1
                    )
                }
                binding.rcvListHastag.adapter = adapterHastag
                hashtagList.addAll(diaryTable.hastagList)
                adapterHastag.updateList(
                    diaryTable.hastagList,
                    diaryTable.textColor,
                    diaryTable.fontTypeface,
                    diaryTable.textSize,
                    diaryTable.textStyle
                )

                binding.rcvListRecord.layoutManager = LinearLayoutManager(this@CreateEditActivity)
                adapterRecord = RecordCreatNewAdapter(this@CreateEditActivity, { position ->
                    if (currentPlayingPosition == position) {
                        stopCurrentSound() // Dừng và giải phóng mediaPlayer
                        currentPlayingPosition = -1 // Reset vị trí đang phát
                    }

                    // Nếu bài hát bị xóa nằm trước bài hát đang phát
                    if (currentPlayingPosition > position) {
                        currentPlayingPosition -= 1 // Điều chỉnh vị trí
                    }
                    soundsList.removeAt(position)
                    adapterRecord.setData(soundsList)
                }, { position ->
                    playSound(position)
                }, {
                    stopCurrentSound()
                }, { _, progress ->
                    mediaPlayer?.seekTo(progress)
                })
                binding.rcvListRecord.adapter = adapterRecord
                val validSounds = diaryTable.listSounds.filter { sound ->
                    val file = File(sound.filePath)
                    file.exists()
                }
                soundsList.clear()
                soundsList.addAll(validSounds)
                adapterRecord.setData(validSounds)

                adapterPhoto = PhotosAdapter(
                    this@CreateEditActivity,
                    diaryTable.fontTypeface,
                    diaryTable.textColor,
                    diaryTable.textDots,
                    diaryTable.alignment,
                    diaryTable.textSize,
                    diaryTable.textStyle
                ) { uri ->
                    binding.rlRoomPhoto.visibility = View.VISIBLE
                    Log.d("Selected Image URI", "Selected URI: $uri")
                    binding.ivSetImage.scaleX = if (uri.isRotated) -1f else 1f
                    Glide.with(this@CreateEditActivity).load(uri.uri)
                        .placeholder(R.drawable.ic_camera_48dp).error(R.drawable.bg_colorapp_radius)
                        .into(binding.ivSetImage)
                }
                binding.rcvListImage.layoutManager = LinearLayoutManager(this@CreateEditActivity)
                binding.rcvListImage.adapter = adapterPhoto
                adapterPhoto.updateList(diaryTable.listPhoto)
                if (diaryTable.backGroundUri?.let { Uri.parse(it).isAbsolute } == true) {
                    backgroundUri = diaryTable.backGroundUri
                    val updatedItem = BackGroundModel(
                        id = 0, image = null, imageUri = diaryTable.backGroundUri
                    )
                    updatedItems[0] = updatedItem
                    adapterBackGround.updateList(updatedItems)
                    backPositon = 0
                    Glide.with(this@CreateEditActivity).load(diaryTable.backGroundUri)
                        .into(object : CustomTarget<Drawable>() {
                            override fun onResourceReady(
                                resource: Drawable, transition: Transition<in Drawable>?
                            ) {
                                //  backgroundUri = resource.toString()
                                binding.activityCreat.background = resource
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                            }
                        })
                } else {
                    background = diaryTable.backGround
                    adapterBackGround.setSelectedPosition(diaryTable.backGround)
                    val backgrounds = ItemsProvider.getDefaultBack()
                    val selectedBackground = backgrounds.filter { it.id != 0 }
                        .find { it.id == diaryTable.backGround }?.image
                    if (selectedBackground != null) {
                        funselectedBackground = selectedBackground
                        binding.activityCreat.setBackgroundResource(selectedBackground)
                    } else {
                        binding.activityCreat.setBackgroundResource(R.color.color_white)
                    }
                }

                if (backgroundUri != null || background != -1) {
                    binding.ivIncludeBackGround.ivRest.visibility = View.VISIBLE
                    binding.ivIncludeBackGround.ivClose.visibility = View.GONE
                } else if (backgroundUri == null && background == -1) {
                    binding.ivIncludeBackGround.ivRest.visibility = View.GONE
                    binding.ivIncludeBackGround.ivClose.visibility = View.VISIBLE
                }

                alignmentInt = diaryTable.alignment
                when (diaryTable.alignment) {
                    1 -> {
                        binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_left)
                        binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_un_center)
                        binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_un_right)
                        changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_VIEW_START
                        )
                        changeTextAlignmentInLayout(
                            binding.ivContent2 as ViewGroup, View.TEXT_ALIGNMENT_VIEW_START
                        )
                    }

                    2 -> {
                        binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_un_left)
                        binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_center)
                        binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_un_right)
                        changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_CENTER
                        )
                        changeTextAlignmentInLayout(
                            binding.ivContent2 as ViewGroup, View.TEXT_ALIGNMENT_CENTER
                        )
                    }

                    3 -> {
                        binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_un_left)
                        binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_un_center)
                        binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_right)
                        changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_VIEW_END
                        )
                        changeTextAlignmentInLayout(
                            binding.ivContent2 as ViewGroup, View.TEXT_ALIGNMENT_VIEW_END
                        )
                    }

                    else -> {
                        binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_left)
                        binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_un_center)
                        binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_un_right)
                        changeTextAlignmentInLayout(
                            binding.ivContent as ViewGroup, View.TEXT_ALIGNMENT_VIEW_START
                        )
                        changeTextAlignmentInLayout(
                            binding.ivContent2 as ViewGroup, View.TEXT_ALIGNMENT_VIEW_START
                        )
                    }
                }

                selectedFont = diaryTable.fontTypeface
                //savedSelectedPositionFont = adapterFontColor.getSelectedPosition()
                adapterFontColor.setSelectedFont(diaryTable.fontTypeface)
                val fontId = resources.getIdentifier(selectedFont, "font", packageName)
                if (fontId != 0) {
                    checkFont2(diaryTable.fontTypeface)
//                    changeTextFontInLayout2(binding.ivContent, diaryTable.fontTypeface)
//                    changeTextFontInLayout2(binding.ivContent2, diaryTable.fontTypeface)
//                    changeTextFontInLayout2(binding.ivDateTime, diaryTable.fontTypeface)
                } else {
                    checkFont2("plus_jakarta_sans_400")
                }
                textStyleInt = diaryTable.textStyle
                Log.d("textStyleInt", "$textStyleInt")
                when (diaryTable.textStyle) {
                    1 -> {
                        isBoldSelected = true
                        isItalicSelected = false
                        binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_print)
                        binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_un_tilt)
                        changeTextStyleInLayout(binding.ivContent as ViewGroup, Typeface.BOLD)
                        changeTextStyleInLayout(binding.ivContent2 as ViewGroup, Typeface.BOLD)
                    }

                    2 -> {
                        isItalicSelected = true
                        isBoldSelected = false
                        binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_un_print)
                        binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_tilt)
                        changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.ITALIC
                        )
                        changeTextStyleInLayout(
                            binding.ivContent2 as ViewGroup, Typeface.ITALIC
                        )
                    }

                    3 -> {
                        isItalicSelected = false
                        isBoldSelected = false
                        binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_un_print)
                        binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_un_tilt)
                        changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.NORMAL
                        )
                        changeTextStyleInLayout(
                            binding.ivContent2 as ViewGroup, Typeface.NORMAL
                        )
                    }

                    else -> {
                        isItalicSelected = false
                        isBoldSelected = false
                        binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_un_print)
                        binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_un_tilt)
                        changeTextStyleInLayout(
                            binding.ivContent as ViewGroup, Typeface.NORMAL
                        )
                        changeTextStyleInLayout(
                            binding.ivContent2 as ViewGroup, Typeface.NORMAL
                        )
                    }
                }
                textSizeInt = diaryTable.textSize
                when (diaryTable.textSize) {
                    1 -> {
                        binding.ivText.ivTextsize1.alpha = 1F
                        binding.ivText.ivTextsize2.alpha = 0.4F
                        binding.ivText.ivTextsize3.alpha = 0.4F
                        changeTextSizeInLayout(binding.ivContent as ViewGroup, 0)
                        changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 0)
                    }

                    2 -> {
                        binding.ivText.ivTextsize1.alpha = 0.4F
                        binding.ivText.ivTextsize2.alpha = 1F
                        binding.ivText.ivTextsize3.alpha = 0.4F
                        changeTextSizeInLayout(binding.ivContent as ViewGroup, 3)
                        changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 3)
                    }

                    3 -> {
                        binding.ivText.ivTextsize1.alpha = 0.4F
                        binding.ivText.ivTextsize2.alpha = 0.4F
                        binding.ivText.ivTextsize3.alpha = 1F
                        changeTextSizeInLayout(binding.ivContent as ViewGroup, 6)
                        changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 6)
                    }

                    else -> {
                        binding.ivText.ivTextsize1.alpha = 1F
                        binding.ivText.ivTextsize2.alpha = 0.4F
                        binding.ivText.ivTextsize3.alpha = 0.4F
                        changeTextSizeInLayout(binding.ivContent as ViewGroup, 0)
                        changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 0)
                    }
                }
                textDotsInt = diaryTable.textDots
                if (diaryTable.textDots == 1) {
                    binding.ivText.ivDot.setImageResource(R.drawable.ic_text_doc_selected)
                    addPrefixToViews(binding.ivContent as ViewGroup)
                    addPrefixToViews(binding.ivContent2 as ViewGroup)
                } else {
                    binding.ivText.ivDot.setImageResource(R.drawable.ic_text_doc)
                    clearPrefixToViews(binding.ivContent as ViewGroup)
                    clearPrefixToViews(binding.ivContent2 as ViewGroup)
                }

                textColorInt = diaryTable.textColor
                val updatedItems = ItemsProvider.getDefaultColors(newColor).map { item ->
                    if (item.id == 1) {
                        item.copy(name = diaryTable.textColor)
                    } else {
                        item
                    }
                }
                adapterTextColor.updateList(updatedItems)
                if (diaryTable.textColor != null) {
                    adapterTextColor.setSelectedColor(diaryTable.textColor)
                    changeTextColorInLayout2(binding.ivContent as ViewGroup, diaryTable.textColor)
                    changeTextColorInLayout2(binding.ivContent2 as ViewGroup, diaryTable.textColor)
                    changeTextColorInLayout2(binding.ivLldate as ViewGroup, diaryTable.textColor)
                } else {

                }
            } catch (e: Exception) {
                Log.d("loadDiaryIdData", "Error loading diary data: ${e.message}")
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.ivScrollView.visibility = View.VISIBLE
                binding.relativeLayout2.visibility = View.VISIBLE
            }
        }
    }

    private fun saveDiaryEntry() {

        try {
            if (diaryId == -1) {
                //Toast.makeText(this, "Invalid diary ID.", Toast.LENGTH_SHORT).show()
                return
            }
            val createdAt = selectedTimeInMillis ?: Calendar.getInstance().timeInMillis
            Log.d("timeInMillis", createdAt.toString())
            val photos = adapterPhoto.getAllPhotos().takeIf { it.isNotEmpty() } ?: emptyList()
            val status = statusEmoji ?: -1
            val title = binding.edtTilte.text.toString().trim()
            val content = binding.edtContent.text.toString().trim()
            val hastag = adapterHastag.getAllHastag().takeIf { it.isNotEmpty() } ?: emptyList()
            val background = background ?: -1
            val backgroundUri = backgroundUri
            val sounds = adapterRecord.getAllSounds().takeIf { it.isNotEmpty() }?.map { sound ->
                sound.copy(progress = 0)
            } ?: emptyList()
            val alignment = alignmentInt ?: 0
            val textStyle = textStyleInt ?: 0
            val textDots = textDotsInt ?: 0
            val textColor = textColorInt
            val textFont = selectedFont
            val textSize = textSizeInt ?: 0
            val diaryEntry = DiaryTable(
                id = diaryId,
                timeInMillis = createdAt,
                statusEmoji = status,
                title = title,
                content = content,
                hastagList = hastag,
                listSounds = sounds,
                listPhoto = photos,
                backGround = background,
                backGroundUri = backgroundUri.toString(),
                alignment = alignment,
                textStyle = textStyle,
                textDots = textDots,
                textSize = textSize,
                textColor = textColor.toString(),
                fontTypeface = textFont.toString(),
            )

            lifecycleScope.launch {
                try {
                    diaryViewModel.insertPF(diaryEntry)
                    Toast.makeText(
                        this@CreateEditActivity, R.string.diary_entry_saved, Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    Log.d("saveDiaryEntry", "Diary entry inserted successfully.")
                } catch (e: Exception) {
                    Toast.makeText(
                        this@CreateEditActivity,
                        R.string.failed_to_save_diary_entry,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("saveDiaryEntry", "Error saving diary entry: ${e.message}")
                }
            }
        } catch (e: Exception) {

        }

    }

    private fun dateTime() {
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        binding.ivDatePick.visibility = View.VISIBLE
        if (selectedTimeInMillis != null) {
            selectedCalendar.timeInMillis = selectedTimeInMillis!!
        }
        binding.datePick.init(
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, month, dayOfMonth ->
            selectedCalendar.set(Calendar.YEAR, year)
            selectedCalendar.set(Calendar.MONTH, month)
            selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedCalendar.set(Calendar.HOUR_OF_DAY, 0)
            selectedCalendar.set(Calendar.MINUTE, 0)
            selectedCalendar.set(Calendar.SECOND, 0)
            selectedCalendar.set(Calendar.MILLISECOND, 0)
        }
        binding.btnCancel.tap {
            binding.ivDatePick.visibility = View.GONE
        }
        binding.btnOk.tap {
            selectedTimeInMillis = selectedCalendar.timeInMillis
            updateDateViews(selectedCalendar)
            Log.d("timeInMillis", selectedTimeInMillis.toString())
            binding.ivDatePick.visibility = View.GONE
        }
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

    private fun recordSound() {
        val dialog = Dialog(this)
        val binding = DialogSoundStartCreatnewBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.btnStart.tap {
                val dia = RecordingDialog(this, this, this)
                dia.show()
                dialog.dismiss()
            }

            dialog.show()
        }

    }

    private fun loadFiles(name: String, filePath: String) {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(this, "${R.string.file_does_not_exist}: $filePath", Toast.LENGTH_SHORT)
                .show()
            return
        }
        var duration: Long = 0
        try {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            duration = mediaPlayer.duration.toLong()
            mediaPlayer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        soundsList.add(
            SoundsModel(
                fileName = name, filePath = filePath, duration = duration, progress = 0
            )
        )
        adapterRecord.setData(soundsList)
    }

    override fun onResume() {
        AppOpenManager.getInstance().enableAppResumeWithActivity(this::class.java)
        super.onResume()
    }

    private fun pickAudioFile() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(this::class.java)
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "audio/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(intent, PICK_AUDIO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_AUDIO_REQUEST_CODE) {
            data?.data?.let { uri ->
                try {
                    val filePath = getRealPathFromURI(uri)
                    if (filePath != null) {
                        val fileName = File(filePath).name
                        val mediaPlayer = MediaPlayer().apply {
                            setDataSource(filePath)
                            prepare()
                        }
                        val duration = mediaPlayer.duration.toLong()
                        mediaPlayer.release()

                        soundsList.add(
                            SoundsModel(
                                fileName = fileName,
                                filePath = filePath,
                                duration = duration,
                                progress = 0
                            )
                        )
                        adapterRecord.setData(soundsList)
                    } else {
                        Toast.makeText(
                            this, R.string.unable_to_retrieve_file_path, Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, R.string.error_processing_the_file, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Audio.Media.DISPLAY_NAME)
        var filePath: String? = null

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val fileName = cursor.getString(columnIndex)

                val tempFile = File(cacheDir, fileName)
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    tempFile.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                filePath = tempFile.absolutePath
            }
        }
        return filePath
    }

    private fun playSound(position: Int) {
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


    private fun statusEmoji() {
        binding.ivIncludeEmoji.root.visibility = View.VISIBLE
        binding.ivIncludeEmoji.ivExit.tap {
            binding.ivIncludeEmoji.root.visibility = View.GONE
        }

        val items = ItemsProvider.getDefaultEmoji()

        adapterEmoji = EmojiCreatNewAdapter(this, items) { emoji ->
            statusEmoji = emoji.id
            savedSelectedPositionEmoji = emoji.id
            binding.ivEmoji.setImageResource(emoji.image)
            imgIcon = emoji.image
        }
        val layoutManager = GridLayoutManager(this, 5)

        adapterEmoji.setSelectedPosition(savedSelectedPositionEmoji)
        binding.ivIncludeEmoji.rcvListEmoji.layoutManager = layoutManager
        binding.ivIncludeEmoji.rcvListEmoji.adapter = adapterEmoji
        adapterEmoji.updateList(items)
    }

    private fun backGround(position: Int, backGroudModel: BackGroundModel) {
        if (position == 0) {
            Log.d("ted", "$position")
            TedImagePicker.with(this).cancelListener {
                Log.d("ted", "image select cancel")
                clearIconSelected()
            }.start { uri ->
                val updatedItems = ItemsProvider.getDefaultBack().toMutableList()
                val updatedItem = BackGroundModel(
                    id = 0, image = null, imageUri = uri.toString()
                )
                updatedItems[0] = updatedItem
                adapterBackGround.updateList(updatedItems)
                Glide.with(this).load(uri).into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable, transition: Transition<in Drawable>?
                    ) {
                        clearIconSelected()

                        background = null
                        backgroundUri = uri.toString()
                        binding.activityCreat.background = resource
                        savedSelectedPositionBackGround = position
                        binding.ivIncludeBackGround.root.visibility = View.GONE
                        binding.ivIncludeBackGround.ivClose.visibility = View.GONE
                        binding.ivIncludeBackGround.ivRest.visibility = View.VISIBLE
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.ivIncludeBackGround.root.visibility = View.GONE
                    }
                })
            }

        } else {

        }

    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val outRect = Rect()
            binding.edtAddhastag.getGlobalVisibleRect(outRect)
            if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                hideHashtagInput()
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideHashtagInput() {
        binding.edtAddhastag.text.clear()
        binding.edtAddhastag.visibility = View.GONE
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edtAddhastag.windowToken, 0)
        if (!SharePrefUtils.isCheckAdsTAG(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_edit
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.btnHastag.setImageResource(R.drawable.ic_hastag_creatnew_ads)
        } else {
            binding.btnHastag.setImageResource(R.drawable.ic_hastag_creatnew)
        }
    }

    private fun hastag() {
        val parsedColor = try {
            if (textColorInt != null) {
                Color.parseColor(textColorInt)
            } else {
                Color.parseColor("#FF7375")
            }
        } catch (e: IllegalArgumentException) {
            Log.e("hastag", "Invalid color format: $textColorInt", e)
            Color.parseColor("#FF7375")
        }

        binding.edtAddhastag.setTextColor(parsedColor)
        binding.edtAddhastag.setHintTextColor(parsedColor)
        Log.d("edtAddhastag", parsedColor.toString())
        binding.edtAddhastag.visibility = View.VISIBLE
        binding.edtAddhastag.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.edtAddhastag, InputMethodManager.SHOW_IMPLICIT)

        binding.edtAddhastag.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val hastag = binding.edtAddhastag.text.toString().trim()
                val newHastag = HastagModel(hashtagList.size + 1, hastag)
                if (hastag.isNotEmpty()) {
                    hashtagList.add(newHastag)
                    adapterHastag.updateList(
                        hashtagList,
                        textColorInt.toString(),
                        selectedFont.toString(),
                        textSizeInt ?: -1,
                        textStyleInt ?: -1
                    )
                    binding.edtAddhastag.text.clear()
                    imm.hideSoftInputFromWindow(binding.edtAddhastag.windowToken, 0)
                    binding.edtAddhastag.visibility = View.GONE
                    clearIconSelected()
                } else {
                    binding.edtAddhastag.text.clear()
                    imm.hideSoftInputFromWindow(binding.edtAddhastag.windowToken, 0)
                    binding.edtAddhastag.visibility = View.GONE
                    clearIconSelected()
                }
                true
            } else {
                false
            }
        }
    }

    private fun textAlignment() {
        binding.ivText.root.visibility = View.VISIBLE
        binding.ivText.ivClose.tap {
            clearIconSelected()
            binding.ivText.root.visibility = View.GONE
        }

        var alignment = View.TEXT_ALIGNMENT_VIEW_START
        binding.ivText.ivAlignmentLeft.setOnClickListener {
            alignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_left)
            binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_un_center)
            binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_un_right)
            changeTextAlignmentInLayout(binding.ivContent as ViewGroup, alignment)
            changeTextAlignmentInLayout(binding.ivContent2 as ViewGroup, alignment)
            alignmentInt = 1
        }

        binding.ivText.ivAlignmentCenter.setOnClickListener {
            alignment = View.TEXT_ALIGNMENT_CENTER
            binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_un_left)
            binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_center)
            binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_un_right)
            changeTextAlignmentInLayout(binding.ivContent as ViewGroup, alignment)
            changeTextAlignmentInLayout(binding.ivContent2 as ViewGroup, alignment)
            alignmentInt = 2
        }

        binding.ivText.ivAlignmentRight.setOnClickListener {
            alignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.ivText.ivAlignmentLeft.setImageResource(R.drawable.ic_align_un_left)
            binding.ivText.ivAlignmentCenter.setImageResource(R.drawable.ic_align_un_center)
            binding.ivText.ivAlignmentRight.setImageResource(R.drawable.ic_align_right)
            changeTextAlignmentInLayout(binding.ivContent as ViewGroup, alignment)
            changeTextAlignmentInLayout(binding.ivContent2 as ViewGroup, alignment)
            alignmentInt = 3
        }

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

    private fun textStyle() {
        binding.ivText.root.visibility = View.VISIBLE
        binding.ivText.ivClose.tap {
            clearIconSelected()
            binding.ivText.root.visibility = View.GONE
        }

        var textStyle = Typeface.NORMAL



        binding.ivText.ivTextBold.setOnClickListener {
            if (!isBoldSelected) {
                // Chọn Bold
                textStyle = Typeface.BOLD
                binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_print)
                binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_un_tilt)
                changeTextStyleInLayout(binding.ivContent as ViewGroup, textStyle)
                changeTextStyleInLayout(binding.ivContent2 as ViewGroup, textStyle)
                textStyleInt = 1
                isItalicSelected = false
            } else {
                // Bỏ Bold
                textStyle = Typeface.NORMAL
                textStyleInt = 3
                Log.d("textStyleIntivTextBold", "$textStyle")
                binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_un_print)
                changeTextStyleInLayout(binding.ivContent as ViewGroup, textStyle)
                changeTextStyleInLayout(binding.ivContent2 as ViewGroup, textStyle)
            }
            isBoldSelected = !isBoldSelected
        }

        binding.ivText.ivTextTilt.setOnClickListener {
            if (!isItalicSelected) {
                // Chọn Italic
                isBoldSelected = false
                textStyle = Typeface.ITALIC
                textStyleInt = 2
                binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_tilt)
                binding.ivText.ivTextBold.setImageResource(R.drawable.ic_text_un_print)
                changeTextStyleInLayout(binding.ivContent as ViewGroup, Typeface.ITALIC)
                changeTextStyleInLayout(binding.ivContent2 as ViewGroup, Typeface.ITALIC)
            } else {
                // Bỏ Italic
                textStyle = Typeface.NORMAL
                textStyleInt = 3
                binding.ivText.ivTextTilt.setImageResource(R.drawable.ic_text_un_tilt)
                changeTextStyleInLayout(binding.ivContent as ViewGroup, Typeface.NORMAL)
                changeTextStyleInLayout(binding.ivContent2 as ViewGroup, Typeface.NORMAL)
            }
            isItalicSelected = !isItalicSelected
        }

    }

    private fun changeTextStyleInLayout(viewGroup: ViewGroup, style: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                val currentTypeface = child.typeface ?: Typeface.DEFAULT
                val updatedTypeface = Typeface.create(currentTypeface, style)
                child.typeface = updatedTypeface
                child.invalidate()
                child.requestLayout()
            } else if (child is TextView) {
                val currentTypeface = child.typeface ?: Typeface.DEFAULT
                val updatedTypeface = Typeface.create(currentTypeface, style)
                child.typeface = updatedTypeface
                child.invalidate()
                child.requestLayout()
            } else if (child is ViewGroup) {
                changeTextStyleInLayout(child, style)
            }
        }
        viewGroup.invalidate()
        viewGroup.requestLayout()
    }

    private fun textDots() {
        var isDotSelected = false

        binding.ivText.root.visibility = View.VISIBLE
        binding.ivText.ivClose.tap {
            clearIconSelected()
            binding.ivText.root.visibility = View.GONE
        }

        binding.ivText.ivDot.tap {
            if (!isDotSelected) {
                binding.ivText.ivDot.setImageResource(R.drawable.ic_text_doc_selected)
                addPrefixToViews(binding.ivContent as ViewGroup)
                addPrefixToViews(binding.ivContent2 as ViewGroup)
                textDotsInt = 1
            } else {
                binding.ivText.ivDot.setImageResource(R.drawable.ic_text_doc)
                clearPrefixToViews(binding.ivContent as ViewGroup)
                clearPrefixToViews(binding.ivContent2 as ViewGroup)
                textDotsInt = 2
            }
            isDotSelected = !isDotSelected
        }
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

    private fun adapterColor() {
        val colorItems = ItemsProvider.getDefaultColors(newColor)
        adapterTextColor = ColorTextCreatNewAdapter(this, colorItems) { color ->
            savedSelectedPositionColor = color.id
            Log.d("adapterTextColor", color.toString())
            if (color.id == 1) {
                val defaultColor = Color.parseColor(color.name)
                popUpColorPicker(defaultColor) { selectedColor ->
                    color.name = selectedColor
                    textColorInt = selectedColor
                    Log.d("newColor", selectedColor)
                    changeTextColorInLayout(binding.ivContent as ViewGroup, selectedColor)
                    changeTextColorInLayout(binding.ivContent2 as ViewGroup, selectedColor)
                    changeTextColorInLayout(binding.ivLldate as ViewGroup, selectedColor)
                    adapterTextColor.updateList(colorItems)
                }
            } else {
                textColorInt = color.name
                changeTextColorInLayout(binding.ivContent as ViewGroup, color.name)
                changeTextColorInLayout(binding.ivContent2 as ViewGroup, color.name)
                changeTextColorInLayout(binding.ivLldate as ViewGroup, color.name)
            }
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.ivText.rcvListTextColor.layoutManager = layoutManager
        binding.ivText.rcvListTextColor.adapter = adapterTextColor
        adapterTextColor.updateList(colorItems)
    }

    private fun textColor() {
        binding.ivText.root.visibility = View.VISIBLE
        binding.ivText.ivClose.tap {
            clearIconSelected()
            binding.ivText.root.visibility = View.GONE
        }
        adapterTextColor.setSelectedPosition(savedSelectedPositionColor)
    }

    private fun popUpColorPicker(defaultColor: Int, onColorPicked: (String) -> Unit) {
        colorPickerPopUp = ColorPickerPopUp(this)
        colorPickerPopUp.setShowAlpha(true).setDefaultColor(defaultColor)
            .setDialogTitle("Pick a Color")
            .setOnPickColorListener(object : ColorPickerPopUp.OnPickColorListener {
                override fun onColorPicked(color: Int) {
                    val colorString = String.format("#%06X", (0xFFFFFF and color))
                    newColor = colorString
                    Log.d("colorString", colorString)
                    onColorPicked(colorString)
                }

                override fun onCancel() {
                    colorPickerPopUp.dismissDialog()
                }
            })
        colorPickerPopUp.show()
    }

    private fun changeTextColorInLayout(viewGroup: ViewGroup, textColor: String) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is EditText) {
                child.setTextColor(Color.parseColor(textColor))
                child.setHintTextColor(Color.parseColor(textColor))
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

    private fun adapterFont() {
        val items = ItemsProvider.getDefaultFont()
        adapterFontColor = FontTextCreatNewAdapter(this, items) { font ->
            selectedFont = font.name
            savedSelectedPositionFont = font.id
            checkFont(font)
        }
        val layoutManager = GridLayoutManager(this, 2)
        adapterFontColor.setSelectedPosition(savedSelectedPositionFont)
        binding.ivText.rcvListTextfont.layoutManager = layoutManager
        binding.ivText.rcvListTextfont.adapter = adapterFontColor
        adapterFontColor.updateList(items)
    }

    private fun checkFont(font: FontTextModel) {
        when (font.id) {
            0 -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "lowan_old_style_700",
                        binding.edtContent to "lowanoldstown_roman"
                    )
                )
                changeTextFontInLayout(binding.ivContent2, font.name)
            }

            1 -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "lemonada_bold_700",
                        binding.edtContent to "lemonada_light"
                    )
                )
                changeTextFontInLayout(binding.ivContent2, font.name)
            }

            3 -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "plus_jakarta_sans_700",
                        binding.edtContent to "plus_jakarta_sans_400"
                    )
                )
                changeTextFontInLayout(binding.ivContent2, font.name)
            }

            else -> {
                changeTextFontInLayout(binding.ivContent, font.name)
                changeTextFontInLayout(binding.ivContent2, font.name)
                // changeTextFontInLayout(binding.ivDateTime, font.name)
            }
        }
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
                changeTextFontInLayout2(binding.ivContent2, font)
            }

            "lemonada_light" -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "lemonada_bold_700",
                        binding.edtContent to "lemonada_light"
                    )
                )
                changeTextFontInLayout2(binding.ivContent2, font)
            }

            "plus_jakarta_sans_400" -> {
                applyCustomFonts(
                    mapOf(
                        binding.edtTilte to "plus_jakarta_sans_700",
                        binding.edtContent to "plus_jakarta_sans_400"
                    )
                )
                changeTextFontInLayout2(binding.ivContent2, font)
            }

            else -> {
                changeTextFontInLayout2(binding.ivContent, font)
                changeTextFontInLayout2(binding.ivContent2, font)
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

    private fun textFont() {
        binding.ivText.root.visibility = View.VISIBLE

        binding.ivText.ivClose.tap {
            clearIconSelected()
            binding.ivText.root.visibility = View.GONE
        }
        adapterFontColor.setSelectedPosition(savedSelectedPositionFont)
    }

    private fun changeTextFontInLayout(viewGroup: ViewGroup, fontTypeface: String) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            val fontId = resources.getIdentifier(fontTypeface, "font", packageName)
            val customFont = ResourcesCompat.getFont(this, fontId)
            if (customFont != null) {
                when (child) {
                    is EditText -> {
                        val currentStyle = child.typeface?.style ?: Typeface.NORMAL
                        val updatedFont = Typeface.create(customFont, currentStyle)
                        child.typeface = updatedFont
                        child.invalidate()
                        child.requestLayout()
                    }

                    is TextView -> {
                        val currentStyle = child.typeface?.style ?: Typeface.NORMAL
                        val updatedFont = Typeface.create(customFont, currentStyle)
                        child.typeface = updatedFont
                        child.invalidate()
                        child.requestLayout()
                    }

                    is ViewGroup -> {
                        changeTextFontInLayout(child, fontTypeface)
                    }
                }
            }
        }
        viewGroup.invalidate()
        viewGroup.requestLayout()
    }

    private fun textSize() {
        binding.ivText.root.visibility = View.VISIBLE
        binding.ivText.ivClose.tap {
            clearIconSelected()
            binding.ivText.root.visibility = View.GONE
        }
        binding.ivText.ivTextsize1.setOnClickListener {
            binding.ivText.ivTextsize1.alpha = 1F
            binding.ivText.ivTextsize2.alpha = 0.4F
            binding.ivText.ivTextsize3.alpha = 0.4F
            changeTextSizeInLayout(binding.ivContent as ViewGroup, 0)
            changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 0)
            textSizeInt = 1
        }

        binding.ivText.ivTextsize2.setOnClickListener {
            binding.ivText.ivTextsize1.alpha = 0.4F
            binding.ivText.ivTextsize2.alpha = 1F
            binding.ivText.ivTextsize3.alpha = 0.4F
            changeTextSizeInLayout(binding.ivContent as ViewGroup, 3)
            changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 3)
            textSizeInt = 2
        }

        binding.ivText.ivTextsize3.setOnClickListener {
            binding.ivText.ivTextsize1.alpha = 0.4F
            binding.ivText.ivTextsize2.alpha = 0.4F
            binding.ivText.ivTextsize3.alpha = 1F
            changeTextSizeInLayout(binding.ivContent as ViewGroup, 6)
            changeTextSizeInLayout(binding.ivContent2 as ViewGroup, 6)
            textSizeInt = 3
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
        viewGroup.requestLayout()
    }

    private fun changeTextColorInLayout2(viewGroup: ViewGroup, textColor: String) {
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
        viewGroup.requestLayout()
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

    override fun dataObservable() {
    }

    override fun save(name: String, filePath: String) {
        loadFiles(name, filePath)
    }

    private fun clearIconSelected() {
        binding.btnBackGround.setImageResource(R.drawable.ic_background_creatnew)
        binding.btnImages.setImageResource(R.drawable.ic_photo_creatnew)
        binding.btnSound.setImageResource(R.drawable.ic_sounds_creat)
        binding.btnText.setImageResource(R.drawable.ic_text_creatnew)
        binding.btnHastag.setImageResource(R.drawable.ic_hastag_creatnew)
        binding.ivIncludeBackGround.root.visibility = View.GONE
        binding.ivIncludeEmoji.root.visibility = View.GONE
        binding.ivSound.root.visibility = View.GONE
        binding.ivText.root.visibility = View.GONE
        binding.ivIncludeBackGround.root.visibility = View.GONE
        showUiADS()
    }

    private fun iconSelected() {
        clearIconSelected()
        when (function) {
            "btnBackGround" -> {
                binding.btnBackGround.setImageResource(R.drawable.ic_background_creatnew_selected)
                binding.ivIncludeBackGround.root.visibility = View.VISIBLE
            }

            "btnImages" -> binding.btnImages.setImageResource(R.drawable.ic_photo_creatnew_selected)
            "btnSound" -> {
                binding.btnSound.setImageResource(R.drawable.ic_sounds_creat_selected)
                binding.ivSound.root.visibility = View.VISIBLE
            }

            "btnText" -> {
                binding.btnText.setImageResource(R.drawable.ic_text_creatnew_selected)
                binding.ivText.root.visibility = View.VISIBLE
            }

            "hastag" -> {
                binding.btnHastag.setImageResource(R.drawable.ic_hastag_creatnew_selected)
            }
        }
    }

    override fun onBackPressed() {
        val dialog = DialogSave(this, this, {
            saveDiaryEntry()
        }) {
            super.onBackPressed()
        }
        dialog.show()

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
                        isCheckCollap = false
                        isCheckNative = true
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
                        isCheckCollap = true
                        isCheckNative = false
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
                        isCheckCollap = false
                        isCheckNative = false
                        binding.frAds.visibility = View.GONE
                        binding.frAds.removeAllViews()
                        binding.include.visibility = View.GONE
                    }
                }
            } else {
                isCheckCollap = false
                isCheckNative = false
                binding.frAds.visibility = View.GONE
                binding.frAds.removeAllViews()
                binding.include.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
            isCheckCollap = false
            isCheckNative = false
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
            binding.include.visibility = View.GONE
            handler.removeCallbacks(runnable)
        }
    }

    private fun showUiADS() {
        if (!SharePrefUtils.isCheckAdsBG(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_edit
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.btnBackGround.setImageResource(R.drawable.ic_background_creatnew_ads)
        } else {
            binding.btnBackGround.setImageResource(R.drawable.ic_background_creatnew)
        }
        if (!SharePrefUtils.isCheckAdsIMG(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_sound_photo
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.btnImages.setImageResource(R.drawable.ic_photo_creatnew_ads)
        } else {
            binding.btnImages.setImageResource(R.drawable.ic_photo_creatnew)
        }

        if (!SharePrefUtils.isCheckAdsTEXT(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_edit
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.btnText.setImageResource(R.drawable.ic_text_creatnew_ads)
        } else {
            binding.btnText.setImageResource(R.drawable.ic_text_creatnew)
        }
        if (!SharePrefUtils.isCheckAdsTAG(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_edit
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.btnHastag.setImageResource(R.drawable.ic_hastag_creatnew_ads)
        } else {
            binding.btnHastag.setImageResource(R.drawable.ic_hastag_creatnew)
        }
        if (!SharePrefUtils.isCheckAdsEMOIJ(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_edit
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.ivEmoji.setImageResource(R.drawable.ic_emotion_ads)
        } else {
            if (imgIcon != -1) {
                binding.ivEmoji.setImageResource(imgIcon)
            } else {
                binding.ivEmoji.setImageResource(R.drawable.ic_emotion)
            }
        }
        if (!SharePrefUtils.isCheckAdsRecord(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_sound_photo
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.ivSound.adsRecord.visible()
        } else {
            binding.ivSound.adsRecord.gone()
        }
        if (!SharePrefUtils.isCheckAdsMusic(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.rewarded_sound_photo
            ) && AdsConsentManager.getConsentResult(this) && SharePrefRemote.get_config(
                this, SharePrefRemote.show_ads
            )
        ) {
            binding.ivSound.adsMusic.visible()
        } else {
            binding.ivSound.adsMusic.gone()
        }
    }
}






