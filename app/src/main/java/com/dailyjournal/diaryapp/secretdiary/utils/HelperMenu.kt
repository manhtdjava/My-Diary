package com.dailyjournal.diaryapp.secretdiary.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import com.dailyjournal.diaryapp.secretdiary.dialog.RatingDialog
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default

class HelperMenu(private val activity: Activity) {

    fun showDialogRate(isFinishActivity: Boolean, life:LifecycleOwner) {
        RatingDialog(activity, isFinishActivity, life).show()
    }

    fun showShareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name))
        var shareMessage = activity.getString(R.string.app_name)
        shareMessage =
            "$shareMessage \nhttps://play.google.com/store/apps/details?id=${activity.packageName}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        activity.startActivity(Intent.createChooser(shareIntent, "Share to"))
    }


    fun showPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Default.PRIVACY_POLICY))
        activity.startActivity(intent)
    }

}