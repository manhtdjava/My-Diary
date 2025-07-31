package com.dailyjournal.diaryapp.secretdiary.ui.intro


import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.amazic.ads.util.AdsConsentManager
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment.Fragment1
import com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment.Fragment2
import com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment.Fragment3
import com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment.Fragment4
import com.dailyjournal.diaryapp.secretdiary.ui.intro.fragment.FragmentAds


class IntroAdapter(fragmentManager: FragmentManager, private val activity: Activity) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        if (SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            return when (position) {
                0 -> Fragment1()
                1 -> FragmentAds(true)
                2 -> Fragment2()
                3 -> FragmentAds(false)
                4 -> Fragment3()
                5 -> Fragment4()
                else -> {
                    Fragment1()
                }
            }
        } else if (!SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full
            ) && !SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            return when (position) {
                0 -> Fragment1()
                1 -> Fragment2()
                2 -> Fragment3()
                3 -> Fragment4()
                else -> {
                    Fragment1()
                }
            }
        } else if (SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full
            ) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            return when (position) {
                0 -> Fragment1()
                1 -> FragmentAds(true)
                2 -> Fragment2()
                3 -> Fragment3()
                4 -> Fragment4()
                else -> {
                    Fragment1()
                }
            }
        } else if (SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            return when (position) {
                0 -> Fragment1()
                1 -> Fragment2()
                2 -> FragmentAds(false)
                3 -> Fragment3()
                4 -> Fragment4()
                else -> {
                    Fragment1()
                }
            }
        } else {
            return when (position) {
                0 -> Fragment1()
                1 -> Fragment2()
                2 -> Fragment3()
                3 -> Fragment4()
                else -> {
                    Fragment1()
                }
            }
        }
    }


    override fun getCount(): Int {
        return if (SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            6

        } else if (!SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full
            ) && !SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full2
            ) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            4
        } else if ((SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full
            ) || SharePrefRemote.get_config(
                activity, SharePrefRemote.native_intro_full2
            )) && AdsConsentManager.getConsentResult(
                activity
            ) && SharePrefRemote.get_config(
                activity, SharePrefRemote.show_ads
            ) && isNetworkAvailable()
        ) {
            5
        } else {
            4
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

}
