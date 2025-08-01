package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.AppOpenManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.databinding.ActivityTedImagePickerBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.adapter.AlbumAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.adapter.GridSpacingItemDecoration
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.adapter.MediaAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.adapter.SelectedMediaAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.base.BaseRecyclerViewAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.TedImagePickerBaseBuilder
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.type.AlbumType
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.type.CameraMedia
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.type.MediaType
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.type.SelectType
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.extenstion.setLock
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.extenstion.toggle
import com.gun0912.tedonactivityresult.model.ActivityResult
import com.tedpark.tedonactivityresult.rx2.TedRxOnActivityResult
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.model.Album
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.model.Media
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.partialaccess.PartialAccessManageBottomSheet
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util.GalleryUtil
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util.MediaUtil
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util.ToastUtil
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util.isPartialAccessGranted
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


internal class TedImagePickerActivity
    : AppCompatActivity(),
    PartialAccessManageBottomSheet.Listener
{
    private lateinit var binding: ActivityTedImagePickerBinding
    private val albumAdapter by lazy { AlbumAdapter(builder) }
    private lateinit var mediaAdapter: MediaAdapter

    //list
    private lateinit var selectedMediaAdapter: SelectedMediaAdapter

    private lateinit var builder: TedImagePickerBaseBuilder<*>

    private lateinit var disposable: Disposable

    private var selectedPosition = 0

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSavedInstanceState(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ted_image_picker)
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            requestedOrientation = builder.screenOrientation
        }
        startAnimation()
        binding.imageCountFormat = builder.imageCountFormat
        setupToolbar()
        setupTitle()
        setupRecyclerView()
        setupListener()
        setupSelectedMediaView()
        setupButton()
        setupAlbumType()
        setupPartialAccessView()
        loadMedia()

        if (SharePrefRemote.get_config(
               this, SharePrefRemote.banner_all
            ) && AdsConsentManager.getConsentResult(this)
        ) {

            runnable = Runnable {
                Admob.getInstance().loadBannerFloor(
                    this, AdmobApi.getInstance().listIDBannerAll
                )
                binding.include.visibility = View.VISIBLE
                handler.postDelayed(runnable, Contants.collap_reload_interval)
            }
            handler.post(runnable)

        } else {
            binding.include.visibility = View.GONE
        }
    }

    private fun startAnimation() {
        if (builder.startEnterAnim != null && builder.startExitAnim != null) {
            overridePendingTransition(builder.startEnterAnim!!, builder.startExitAnim!!)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(builder.showTitle)
        builder.backButtonResId.let {
            binding.toolbar.setNavigationIcon(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupTitle() {
        val title = builder.title ?: getString(builder.titleResId)
        setTitle(title)
    }

    private fun setupButton() {
        with(binding) {
            buttonGravity = builder.buttonGravity
            buttonText = builder.buttonText ?: getString(builder.buttonTextResId)
            buttonTextColor =
                ContextCompat.getColor(this@TedImagePickerActivity, builder.buttonTextColorResId)
            buttonBackground = builder.buttonBackgroundResId
            buttonDrawableOnly = builder.buttonDrawableOnly
        }

        setupButtonVisibility()
    }

    private fun setupButtonVisibility() {
        binding.showButton = when {
            builder.selectType == SelectType.SINGLE -> false
            else -> mediaAdapter.selectedUriList.isNotEmpty()
        }
    }

    private fun loadMedia(isRefresh: Boolean = false) {
        disposable = GalleryUtil.getMedia(this, builder.mediaType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { albumList: List<Album> ->
                albumAdapter.replaceAll(albumList)
                setSelectedAlbum(selectedPosition)
                if (!isRefresh) {
                    setSelectedUriList(builder.selectedUriList)
                }
                binding.layoutContent.rvMedia.visibility = View.VISIBLE

            }
    }

    private fun setSelectedUriList(uriList: List<Uri>?) =
        uriList?.forEach { uri: Uri -> onMultiMediaClick(uri) }

    private fun setSavedInstanceState(savedInstanceState: Bundle?) {

        val bundle: Bundle? = when {
            savedInstanceState != null -> savedInstanceState
            else -> intent.extras
        }

        builder = bundle?.getParcelable(EXTRA_BUILDER)
            ?: TedImagePickerBaseBuilder<TedImagePickerBaseBuilder<*>>()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(EXTRA_BUILDER, builder)
        super.onSaveInstanceState(outState)
    }

    private fun setupRecyclerView() {
        setupAlbumRecyclerView()
        setupMediaRecyclerView()
        setupSelectedMediaRecyclerView()
    }


    private fun setupAlbumRecyclerView() {

        val albumAdapter = albumAdapter.apply {
            onItemClickListener = object : BaseRecyclerViewAdapter.OnItemClickListener<Album> {
                override fun onItemClick(data: Album, itemPosition: Int, layoutPosition: Int) {
                    this@TedImagePickerActivity.setSelectedAlbum(itemPosition)
                    binding.drawerLayout.close()
                    binding.isAlbumOpened = false
                }
            }
        }
        binding.rvAlbum.run {
            adapter = albumAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    binding.drawerLayout.setLock(newState == RecyclerView.SCROLL_STATE_DRAGGING)
                }
            })
        }

        binding.rvAlbumDropDown.adapter = albumAdapter

    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(TedImagePickerActivity::class.java)
    }

    private fun setupMediaRecyclerView() {
        mediaAdapter = MediaAdapter(this, builder).apply {
            onItemClickListener = object : BaseRecyclerViewAdapter.OnItemClickListener<Media> {
                override fun onItemClick(data: Media, itemPosition: Int, layoutPosition: Int) {
                    binding.isAlbumOpened = false
                    this@TedImagePickerActivity.onMediaClick(data.uri)
                }

                override fun onHeaderClick() {
                    AppOpenManager.getInstance().disableAppResumeWithActivity(TedImagePickerActivity::class.java)
                    onCameraTileClick()
                }
            }

            onMediaAddListener = {
                binding.layoutContent.rvSelectedMedia.smoothScrollToPosition(selectedMediaAdapter.itemCount)
            }

        }

        binding.layoutContent.rvMedia.run {
            layoutManager = GridLayoutManager(this@TedImagePickerActivity, IMAGE_SPAN_COUNT)
            addItemDecoration(GridSpacingItemDecoration(IMAGE_SPAN_COUNT, 26))
            itemAnimator = null
            adapter = mediaAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    (layoutManager as? LinearLayoutManager)?.let {
                        val firstVisiblePosition = it.findFirstCompletelyVisibleItemPosition()
                        if (firstVisiblePosition <= 0) {
                            return
                        }
                        val media = mediaAdapter.getItem(firstVisiblePosition)
                        val dateString = SimpleDateFormat(
                            builder.scrollIndicatorDateFormat,
                            Locale.getDefault()
                        ).format(Date(TimeUnit.SECONDS.toMillis(media.dateAddedSecond)))
                        binding.layoutContent.fastScroller.setBubbleText(dateString)
                    }
                }
            })
        }

        binding.layoutContent.fastScroller.recyclerView = binding.layoutContent.rvMedia

    }

    private fun setupSelectedMediaRecyclerView() {
        binding.layoutContent.selectType = builder.selectType

        selectedMediaAdapter = SelectedMediaAdapter().apply {
            onClearClickListener = { uri ->
                onMultiMediaClick(uri)
            }
        }
        binding.layoutContent.rvSelectedMedia.run {
            layoutManager = LinearLayoutManager(
                this@TedImagePickerActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = selectedMediaAdapter

        }

    }

    @SuppressLint("CheckResult")
    private fun onCameraTileClick() {
        val cameraMedia = when (builder.mediaType) {
            MediaType.IMAGE -> CameraMedia.IMAGE
            MediaType.VIDEO -> CameraMedia.VIDEO
            MediaType.IMAGE_AND_VIDEO -> CameraMedia.IMAGE
        }
        val (cameraIntent, uri) = MediaUtil.getMediaIntentUri(
            this@TedImagePickerActivity,
            cameraMedia,
            builder.savedDirectoryName
        )
        TedRxOnActivityResult.with(this@TedImagePickerActivity)
            .startActivityForResult(cameraIntent)
            .subscribe { activityResult: ActivityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    MediaUtil.scanMedia(this, uri)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            loadMedia(true)
                            onMediaClick(uri)
                        }
                }
            }
    }


    private fun onMediaClick(uri: Uri) {
        when (builder.selectType) {
            SelectType.SINGLE -> onSingleMediaClick(uri)
            SelectType.MULTI -> onMultiMediaClick(uri)
        }
    }

    private fun onMultiMediaClick(uri: Uri) {
        mediaAdapter.toggleMediaSelect(uri)
        binding.layoutContent.items = mediaAdapter.selectedUriList
        updateSelectedMediaView()
        setupButtonVisibility()
    }

    private fun setupSelectedMediaView() {
        binding.layoutContent.viewSelectedMedia.run {
            if (mediaAdapter.selectedUriList.size > 0) {
                layoutParams.height = 78
            } else {
                layoutParams.height = 0
            }
            requestLayout()
        }
    }

    private fun updateSelectedMediaView() {
        binding.layoutContent.viewSelectedMedia.post {
            binding.layoutContent.viewSelectedMedia.run {
                if (mediaAdapter.selectedUriList.size > 0) {
                    slideView(
                        this,
                        layoutParams.height, 78
                    )
                } else if (mediaAdapter.selectedUriList.size == 0) {
                    slideView(this, layoutParams.height, 0)
                }
            }
        }
    }

    private fun slideView(view: View, currentHeight: Int, newHeight: Int) {
        val valueAnimator = ValueAnimator.ofInt(currentHeight, newHeight).apply {
            addUpdateListener {
                view.layoutParams.height = it.animatedValue as Int
                view.requestLayout()
            }
        }

        AnimatorSet().apply {
            interpolator = AccelerateDecelerateInterpolator()
            play(valueAnimator)
        }.start()
    }

    private fun onSingleMediaClick(uri: Uri) {
        val data = Intent().apply {
            putExtra(EXTRA_SELECTED_URI, uri)
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun finish() {
        super.finish()
        finishAnimation()
    }

    private fun finishAnimation() {
        if (builder.finishEnterAnim != null && builder.finishExitAnim != null) {
            overridePendingTransition(builder.finishEnterAnim!!, builder.finishExitAnim!!)
        }
    }

    private fun onMultiMediaDone() {

        val selectedUriList = mediaAdapter.selectedUriList
        if (selectedUriList.size < builder.minCount) {
            val message = builder.minCountMessage ?: getString(builder.minCountMessageResId)
            ToastUtil.showToast(message)
        } else {

            val data = Intent().apply {
                putParcelableArrayListExtra(
                    EXTRA_SELECTED_URI_LIST,
                    ArrayList(selectedUriList)
                )
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }

    }


    private fun setSelectedAlbum(selectedPosition: Int) {
        val album = albumAdapter.getItem(selectedPosition)
        if (this.selectedPosition == selectedPosition && binding.selectedAlbum == album) {
            return
        }

        binding.selectedAlbum = album
        this.selectedPosition = selectedPosition
        albumAdapter.setSelectedAlbum(album)
        mediaAdapter.replaceAll(album.mediaUris)
        binding.layoutContent.rvMedia.layoutManager?.scrollToPosition(0)
    }

    private fun setupListener() {
        binding.viewSelectedAlbum.setOnClickListener {
            binding.drawerLayout.toggle()
        }

        binding.viewDoneTop.root.setOnClickListener {
            onMultiMediaDone()
        }
        binding.viewDoneBottom.root.setOnClickListener {
            onMultiMediaDone()
        }

        binding.viewSelectedAlbumDropDown.setOnClickListener {
            binding.isAlbumOpened = !binding.isAlbumOpened
        }

    }

    private fun setupAlbumType() {
        if (builder.albumType == AlbumType.DRAWER) {
            binding.viewSelectedAlbumDropDown.visibility = View.GONE
        } else {
            binding.viewBottom.visibility = View.GONE
            binding.drawerLayout.setLock(true)
        }
    }

    private fun setupPartialAccessView() =
        with(binding.layoutContent.layoutTedImagePickerPartialAccessManage) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                root.isGone = true
                return@with
            }
            root.isVisible = builder.mediaType.isPartialAccessGranted
            tvPartialAccessManage.setOnClickListener { showPartialAccessManageDialog() }
            val mediaTypeText = getString(builder.mediaType.nameResId)
            tvPartialAccessNotice.text =
                getString(R.string.ted_image_picker_partial_access_notice_fmt, mediaTypeText)
        }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun showPartialAccessManageDialog() {
         PartialAccessManageBottomSheet.show(this, builder.mediaType)
    }

    override fun onRefreshMedia() {
        loadMedia(true)
        setupPartialAccessView()
    }

    override fun onBackPressed() {
        if (isAlbumOpened()) {
            closeAlbum()
        } else {
            super.onBackPressed()
        }

    }

    private fun isAlbumOpened(): Boolean =
        if (builder.albumType == AlbumType.DRAWER) {
            binding.drawerLayout.isOpen()
        } else {
            binding.isAlbumOpened
        }

    private fun closeAlbum() {

        if (builder.albumType == AlbumType.DRAWER) {
            binding.drawerLayout.close()
        } else {
            binding.isAlbumOpened = false
        }
    }

    override fun onDestroy() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
        if (handler != null && ::runnable.isInitialized) handler.removeCallbacks(runnable)
        super.onDestroy()
    }


    companion object {
        private const val IMAGE_SPAN_COUNT = 3
        private const val EXTRA_BUILDER = "EXTRA_BUILDER"
        private const val EXTRA_SELECTED_URI = "EXTRA_SELECTED_URI"
        private const val EXTRA_SELECTED_URI_LIST = "EXTRA_SELECTED_URI_LIST"

        internal fun getIntent(context: Context, builder: TedImagePickerBaseBuilder<*>) =
            Intent(context, TedImagePickerActivity::class.java)
                .apply {
                    putExtra(EXTRA_BUILDER, builder)
                }

        internal fun getSelectedUri(data: Intent): Uri? =
            data.getParcelableExtra(EXTRA_SELECTED_URI)

        internal fun getSelectedUriList(data: Intent): List<Uri>? =
            data.getParcelableArrayListExtra(EXTRA_SELECTED_URI_LIST)
    }
}





