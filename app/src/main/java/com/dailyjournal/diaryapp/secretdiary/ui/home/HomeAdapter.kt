package com.dailyjournal.diaryapp.secretdiary.ui.home


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dailyjournal.diaryapp.secretdiary.ui.home.fragment.CalenderFragment
import com.dailyjournal.diaryapp.secretdiary.ui.home.fragment.HomeFragment
import com.dailyjournal.diaryapp.secretdiary.ui.home.fragment.MineFragment
import com.dailyjournal.diaryapp.secretdiary.ui.home.fragment.PhotoFragment

class HomeAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                HomeFragment()
            }

            1 -> CalenderFragment()
            2 -> {
                PhotoFragment()
            }

            3 -> {
                MineFragment()
            }

            else -> {
                HomeFragment()
            }
        }
    }


    override fun getCount(): Int {
        return 4
    }
}
