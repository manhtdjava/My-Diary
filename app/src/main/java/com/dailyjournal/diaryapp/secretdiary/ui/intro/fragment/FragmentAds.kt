package com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.Admob
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.databinding.LayoutItemIntroBinding
import com.dailyjournal.diaryapp.secretdiary.model.IntroModel
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.visible


@SuppressLint("UseCompatLoadingForDrawables")
open class FragmentAds(val idAds: Boolean) : Fragment() {

    private lateinit var binding: LayoutItemIntroBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = LayoutItemIntroBinding.inflate(inflater, container, false)
        try {
            initView()
            viewListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }
    private fun initView() {
        try {
            val data = IntroModel(
                R.drawable.img_intro_1, R.string.content_intro_1, R.string.title_intro_1, type = 1
            )
            if (data.type == 0) {
                binding.llContent.visible()
                binding.frAds.gone()
                binding.ivIntro.setImageResource(data.image)
                binding.tvTile.setText(data.content)
                binding.tvContent.setText(data.title)
            } else {
                binding.llContent.gone()
                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    activity,
                    binding.frAds,
                    R.layout.ads_native_large_full_shimer,
                    R.layout.layout_native_large_full,
                    R.layout.layout_native_large_full
                )
                if (idAds){
                    nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_intro_full"))
                }else{
                    nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_intro_full2"))
                }
                val nativeManager = NativeManager(requireActivity(), this, nativeBuilder)
            }
        } catch (_: Exception) {

        }
    }

    private fun viewListener() {

    }
}