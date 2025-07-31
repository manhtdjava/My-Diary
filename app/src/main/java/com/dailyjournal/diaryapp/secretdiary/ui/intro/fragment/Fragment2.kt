package com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.databinding.LayoutItemIntroBinding
import com.dailyjournal.diaryapp.secretdiary.model.IntroModel
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.gone
import com.dailyjournal.diaryapp.secretdiary.widget.visible


@SuppressLint("UseCompatLoadingForDrawables")
open class Fragment2 : Fragment() {

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
                R.drawable.img_intro_2, R.string.content_intro_2, R.string.title_intro_2, type = 0
            )
            if (data.type == 0) {
                binding.llContent.visible()
                binding.frAds.gone()
                binding.ivIntro.setImageResource(data.image)
                binding.tvTile.setText(data.content)
                binding.tvContent.setText(data.title)
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun viewListener() {

    }
}