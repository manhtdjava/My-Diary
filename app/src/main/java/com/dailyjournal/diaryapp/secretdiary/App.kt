package com.dailyjournal.diaryapp.secretdiary

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.amazic.ads.billing.AppPurchase
import com.amazic.ads.util.AdsApplication
import com.amazic.ads.util.AppOpenManager
import com.dailyjournal.diaryapp.secretdiary.ui.splash.SplashActivity
import com.google.android.gms.ads.MobileAds

class App: AdsApplication() {
    private val PRODUCT_ID_MONTH = "android.test.purchased"
    override fun onCreate() {
        super.onCreate()
        createNotification()
        MobileAds.initialize(this) { }
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        val listINAPId: MutableList<String> = ArrayList()
        listINAPId.add(PRODUCT_ID_MONTH)
        val listSubsId: MutableList<String> = ArrayList()
        listSubsId.add(PRODUCT_ID_MONTH)
        AppPurchase.getInstance().initBilling(this, listINAPId, listSubsId)
        val appToken = getString(R.string.adjust)
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION

        val config = AdjustConfig(this, appToken, environment)
        Adjust.onCreate(config)
    }

    override fun getAppTokenAdjust(): String {
        return ""
    }

    override fun getFacebookID(): String {
        return ""
    }

    override fun buildDebug(): Boolean? {
        return null
    }

    private fun createNotification() {
        val name = "Notification Channel"
        val des = "ABC"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("channel1", name, importance)
        channel.description = des
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

}