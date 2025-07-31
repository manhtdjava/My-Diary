package com.dailyjournal.diaryapp.secretdiary.ui.home.fragment

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.ads.InterAdHelper
import com.dailyjournal.diaryapp.secretdiary.data.DiaryTable
import com.dailyjournal.diaryapp.secretdiary.databinding.FragmentHomeBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.model.DiaryItem
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.CreateGetActivity
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter.DiaryAdapter
import com.dailyjournal.diaryapp.secretdiary.utils.SystemUtil
import com.dailyjournal.diaryapp.secretdiary.viewmodel.DiaryViewModel
import java.util.Calendar


@SuppressLint("UseCompatLoadingForDrawables")
open class HomeFragment : Fragment() {
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        try {
            initView()
            viewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return binding.root
    }

    private fun initView() {
        LogEven.logEvent(requireActivity(), "home_open", Bundle())
        val count = SharePrefUtils.getSplashOpenCount(requireActivity())
        if (count <= 10) {
            LogEven.logEvent(requireActivity(), "home_open_$count", Bundle())
        }
        LogEven.logEvent(requireActivity(), "home_view", Bundle())
        binding.textView.isSelected = true
        SystemUtil.setLocale(requireActivity())
        if (SharePrefUtils.getTheme(requireActivity()) == 2 || SharePrefUtils.getTheme(
                requireActivity()
            ) == 5
        ) {
            binding.textView.setTextColor(requireActivity().getColor(R.color.color_F4F5F5))
            binding.tvUser.setTextColor(requireActivity().getColor(R.color.color_F4F5F5))
        } else {
            binding.textView.setTextColor(requireActivity().getColor(R.color.color_292B2B))
            binding.tvUser.setTextColor(requireActivity().getColor(R.color.color_292B2B))
        }

        binding.tvUser.text = if (SharePrefUtils.getUser(requireActivity()).isNotEmpty()) {
            String.format(getString(R.string.hello_user), SharePrefUtils.getUser(requireActivity()))
        } else {
            String.format(getString(R.string.hello_user), "User")
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.itemPremium.visibility = View.GONE
        binding.itemBottom.visibility = View.GONE
        diaryAdapter = DiaryAdapter(requireActivity(), this) { id ->
            showLoading()
            if (SharePrefRemote.get_config(
                    requireActivity(), SharePrefRemote.inter_preview
                ) && InterAdHelper.canShowNextAd(requireActivity()) && AdsConsentManager.getConsentResult(
                    requireActivity()
                )
            ) {
                InterAdHelper.showListInterAd(
                    requireActivity(), SharePrefRemote.get_config(
                        requireActivity(), SharePrefRemote.inter_preview
                    ), AdmobApi.getInstance().getListIDByName("inter_create")
                ) {
                    val inten = Intent(requireActivity(), CreateGetActivity::class.java).apply {
                        putExtra("IDDIARY", id)
                    }
                    startActivity(inten)
                    hideLoading()
                }
            } else {
                val inten = Intent(requireActivity(), CreateGetActivity::class.java).apply {
                    putExtra("IDDIARY", id)
                }
                startActivity(inten)
                hideLoading()
            }
        }
        binding.rcvListHome.adapter = diaryAdapter
        binding.rcvListHome.layoutManager = LinearLayoutManager(requireActivity())
        diaryViewModel = ViewModelProvider(requireActivity())[DiaryViewModel::class.java]
        try {
            diaryViewModel.allCVs.observe(viewLifecycleOwner) { diaries ->
                binding.progressBar.visibility = View.GONE
                if (diaries.isNotEmpty()) {
                    val groupedData = groupDiaryByYear(diaries)
                    diaryAdapter.updateList(groupedData)
//                    binding.icPremium.visibility = View.VISIBLE
//                    binding.itemPremium.visibility = View.GONE
                    binding.itemBottom.visibility = View.GONE
                } else {
                    diaryAdapter.updateList(emptyList())
//                    binding.icPremium.visibility = View.GONE
//                    binding.itemPremium.visibility = View.VISIBLE
                    binding.itemBottom.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            Log.d("loadDiaryIdData", "Error loading diary data: ${e.message}")
        }
    }


    private fun viewListener() {

    }

    private fun groupDiaryByYear(diaries: List<DiaryTable>): List<DiaryItem> {
        val sortedDiaries = diaries.sortedByDescending { it.timeInMillis }
        val list = sortedDiaries.groupBy { diary ->
            val calendar = Calendar.getInstance().apply { timeInMillis = diary.timeInMillis }
            calendar.get(Calendar.YEAR)
        }.flatMap { (year, entries) ->
            mutableListOf<DiaryItem>(DiaryItem.Header(year)) + entries.map { DiaryItem.Diary(it) }
        }
        return if (SharePrefRemote.get_config(
                requireActivity(), SharePrefRemote.native_home
            ) && AdsConsentManager.getConsentResult(requireActivity())
        ) {
            addAdsToDiaries(list)
        } else {
            list
        }
    }

    private fun addAdsToDiaries(diaries: List<DiaryItem>): List<DiaryItem> {
        val result = mutableListOf<DiaryItem>()
        var i=0
        diaries.forEachIndexed { index, diaryItem ->
            if (index == 2) {
                result.add(DiaryItem.ads(ads = 0))
                i++
            }
            if(index!=0&& checkItemType(diaryItem)){
                i--
            }

            if (index != 2 && index != 1 && (index + i) % 4 == 2) {
                i++
                result.add(DiaryItem.ads(ads = 0))
            }
            result.add(diaryItem)
        }

        return result
    }

    fun checkItemType(item: DiaryItem) : Boolean {
        return when (item) {
            is DiaryItem.Header -> {
                true
            }

            is DiaryItem.Diary -> {
                false
            }

            is DiaryItem.ads -> {
                false
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            SystemUtil.setLocale(requireActivity())
        }
        binding.tvUser.text = if (SharePrefUtils.getUser(requireActivity()).isNotEmpty()) {
            String.format(getString(R.string.hello_user), SharePrefUtils.getUser(requireActivity()))
        } else {
            String.format(getString(R.string.hello_user), "User")
        }
        diaryAdapter.notifyDataSetChanged()
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