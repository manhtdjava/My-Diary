package com.dailyjournal.diaryapp.secretdiary.ui.home.fragment

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dailyjournal.diaryapp.secretdiary.databinding.FragmentHomeBinding


@SuppressLint("UseCompatLoadingForDrawables")
open class MineFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: FragmentHomeBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
     try {
         initView()
         viewListener()
     }catch (e:Exception){ e.printStackTrace()}
        return binding.root
    }

    private fun initView() {

    }

    private fun viewListener() {

    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {

    }
}