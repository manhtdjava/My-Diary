package com.dailyjournal.diaryapp.secretdiary.ui.home.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.FragmentPhotosBinding
import com.dailyjournal.diaryapp.secretdiary.dialog.DialogDelete
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.home.photos.FragmentPhotoAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.home.photos.ShowPhotoGallerActivity
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.viewmodel.DiaryViewModel
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import kotlinx.coroutines.launch
import java.util.Calendar


@SuppressLint("UseCompatLoadingForDrawables")
open class PhotoFragment : Fragment() {
    private lateinit var binding: FragmentPhotosBinding
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var fragmentPhotoAdapter: FragmentPhotoAdapter
    private val selectedPhotos = mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)
        try {
            initView()
            viewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    private fun initView() {
        LogEven.logEvent(requireActivity(), "photo_view", Bundle())
        SystemUtil.setLocale(requireActivity())
        if (SharePrefUtils.getTheme(requireActivity()) == 2 || SharePrefUtils.getTheme(
                requireActivity()
            ) == 5
        ) {
            binding.textView.setTextColor(requireActivity().getColor(R.color.color_F4F5F5))
        } else {
            binding.textView.setTextColor(requireActivity().getColor(R.color.color_292B2B))
        }
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        fragmentPhotoAdapter = FragmentPhotoAdapter(requireActivity(),
            this,
            selectedPhotos,
            onClick = {},
            onClickPosition = { photoUri ->
                LogEven.logEvent(requireActivity(), "image_photo_click", Bundle())
                if (selectedPhotos.contains(photoUri)) {
                    selectedPhotos.remove(photoUri)
                } else {
                    selectedPhotos.add(photoUri)
                }
                fragmentPhotoAdapter.notifyDataSetChanged()
                updateDeleteButtonVisibility()
            },
            { photoList, id ->
                showLoading()
                if (SharePrefRemote.get_config(
                        requireActivity(), SharePrefRemote.inter_libruary
                    ) && InterAdHelper.canShowNextAd(requireActivity()) && AdsConsentManager.getConsentResult(
                        requireActivity()
                    )
                ) {
                    InterAdHelper.showListInterAd(
                        requireActivity(), SharePrefRemote.get_config(
                            requireActivity(), SharePrefRemote.inter_libruary
                        ), AdmobApi.getInstance().getListIDByName("inter_create")
                    ) {
                        val allPhotos = diaryViewModel.allCVs.value?.flatMap { diary ->
                            diary.listPhoto.flatMap { it.listUri }
                        } ?.sortedByDescending { it.timeInMillis }
                            ?: emptyList()
                        val startIndex = allPhotos.indexOfFirst { it.id == photoList.id }

                        if (startIndex != -1) {
                            val intent = Intent(
                                requireActivity(), ShowPhotoGallerActivity::class.java
                            ).apply {
                                putParcelableArrayListExtra("photoList", ArrayList(allPhotos))
                                putExtra("startIndex", startIndex)
                            }
                            startActivity(intent)
                        } else {
                            Log.e("PhotoGallery", "Photo not found in list!")
                        }
                        hideLoading()
                    }
                } else {
                    val allPhotos = diaryViewModel.allCVs.value?.flatMap { diary ->
                        diary.listPhoto.flatMap { it.listUri }
                    }  ?.sortedByDescending { it.timeInMillis }
                        ?: emptyList()
                    val startIndex = allPhotos.indexOfFirst { it.id == photoList.id }

                    if (startIndex != -1) {
                        val intent =
                            Intent(requireActivity(), ShowPhotoGallerActivity::class.java).apply {
                                putParcelableArrayListExtra("photoList", ArrayList(allPhotos))
                                putExtra("startIndex", startIndex)
                            }
                        startActivity(intent)
                    } else {
                        Log.e("PhotoGallery", "Photo not found in list!")
                    }
                    hideLoading()
                }

            })
        binding.rcv.adapter = fragmentPhotoAdapter
        binding.rcv.layoutManager = LinearLayoutManager(requireActivity())
        diaryViewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]
        binding.progressBar.visibility = View.VISIBLE
        try {
            diaryViewModel.allCVs.observe(viewLifecycleOwner) { diaries ->
                binding.progressBar.visibility = View.GONE
                val diariesWithPhotos = diaries.filter { diary ->
                    diary.listPhoto.isNotEmpty()
                }
                if (diariesWithPhotos.isNotEmpty()) {
                    fragmentPhotoAdapter.updateList(processDiaries(diariesWithPhotos))
                    binding.noPictures.visibility = View.GONE
                    binding.rcv.visibility = View.VISIBLE
                } else {
                    fragmentPhotoAdapter.updateList(emptyList())
                    binding.noPictures.visibility = View.VISIBLE
                    binding.rcv.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            Log.d("loadDiaryIdData", "Error loading diary data: ${e.message}")
        }
    }

    private fun viewListener() {
        binding.ivDelete.tap {
            LogEven.logEvent(requireActivity(), "delete_photo_click", Bundle())
            val dialog = DialogDelete(requireActivity(), viewLifecycleOwner, {
                deleteImages()
            }) {

            }
            dialog.show()
        }
    }

    private fun processDiaries(diariesWithPhotos: List<DiaryTable>): List<DiaryTable> {
        val result = mutableListOf<DiaryTable>()
        val sortedDiaries = diariesWithPhotos.sortedByDescending { it.timeInMillis }
        val groupedDiaries = sortedDiaries.groupBy { diary ->
            val calendar = Calendar.getInstance().apply { timeInMillis = diary.timeInMillis }
            calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH) to calendar.get(Calendar.DAY_OF_MONTH)
        }.flatMap { (timeGroup, groupedDiaries) ->
            mutableListOf(
                DiaryTable(
                    id = timeGroup.hashCode(),
                    timeInMillis = groupedDiaries.first().timeInMillis,
                    statusEmoji = groupedDiaries.first().statusEmoji,
                    title = groupedDiaries.first().title,
                    content = groupedDiaries.first().content,
                    hastagList = groupedDiaries.first().hastagList,
                    listSounds = groupedDiaries.first().listSounds,
                    listPhoto = groupedDiaries.flatMap { it.listPhoto },
                    backGround = groupedDiaries.first().backGround,
                    backGroundUri = groupedDiaries.first().backGroundUri,
                    alignment = groupedDiaries.first().alignment,
                    textStyle = groupedDiaries.first().textStyle,
                    textSize = groupedDiaries.first().textSize,
                    textDots = groupedDiaries.first().textDots,
                    textColor = groupedDiaries.first().textColor,
                    fontTypeface = groupedDiaries.first().fontTypeface,
                    selectedPhotos = groupedDiaries.first().selectedPhotos
                )
            )
        }
        if (SharePrefRemote.get_config(
                requireActivity(), SharePrefRemote.native_libruary
            ) && AdsConsentManager.getConsentResult(requireActivity())&& SharePrefRemote.get_config(
                requireActivity(), SharePrefRemote.show_ads
            )
        ) {
            groupedDiaries.forEachIndexed { index, diaryItem ->
                if ((index) % 3 == 0) {
                    val emptyItem = DiaryTable(
                        id = -1,
                        timeInMillis = 0L,
                        statusEmoji = 0,
                        title = "No Photos",
                        content = "This diary has no photos.",
                        hastagList = emptyList(),
                        listSounds = emptyList(),
                        listPhoto = emptyList(),
                        backGround = 0,
                        backGroundUri = "",
                        alignment = 0,
                        textStyle = 0,
                        textSize = 0,
                        textDots = 0,
                        textColor = "#000000",
                        fontTypeface = "",
                        selectedPhotos = mutableListOf()
                    )
                    result.add(emptyItem)
                }
                result.add(diaryItem)
            }

        } else {
            return groupedDiaries
        }
        return result
    }

    private fun deleteImages() {
        Log.d("SelectedPhotos", "Photos to delete: $selectedPhotos")
        if (selectedPhotos.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    val allDiaries = diaryViewModel.allCVs.value ?: emptyList()
                    val updatedDiaries = allDiaries.map { diary ->
                        val updatedPhotos = diary.listPhoto.map { photo ->
                            val filteredUris = photo.listUri.filterNot { photoItem ->
                                selectedPhotos.contains(photoItem.id)
                            }
                            photo.copy(listUri = filteredUris.toMutableList())
                        }.filter { it.listUri.isNotEmpty() }

                        diary.copy(listPhoto = updatedPhotos)
                    }

                    updatedDiaries.forEach { updatedDiary ->
                        diaryViewModel.updateDiary(updatedDiary)
                    }
                    selectedPhotos.clear()
                    fragmentPhotoAdapter.notifyDataSetChanged()
                    updateDeleteButtonVisibility()
                    Toast.makeText(
                        requireContext(), R.string.selected_photos_deleted, Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Log.e("PhotoDeleteError", "Error deleting photos: ${e.message}")
                }
            }
        } else {
            Toast.makeText(requireContext(), R.string.no_photos_selected, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDeleteButtonVisibility() {
        if (selectedPhotos.isNotEmpty()) {
            binding.ivDelete.visibility = View.VISIBLE
        } else {
            binding.ivDelete.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            SystemUtil.setLocale(requireActivity())
        }
        fragmentPhotoAdapter.notifyDataSetChanged()
        super.onResume()
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
                loadingLayout, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
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