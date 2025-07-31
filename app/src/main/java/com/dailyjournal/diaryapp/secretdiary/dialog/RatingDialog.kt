package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseDialog
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogRatingBinding
import com.dailyjournal.diaryapp.secretdiary.logenven.LogEven
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefUtils
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Default
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class RatingDialog(
    activity: Activity,
    private val isFinishActivity: Boolean,
    private val life: LifecycleOwner
) :
    BaseDialog<DialogRatingBinding>(activity, true) {
    override fun getContentView(): DialogRatingBinding {
        return DialogRatingBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
        //  loadNative()
        changeRating()
        binding.rtb.rating = 5f
        var bundle = Bundle()
        bundle.putString("position", "Setting")
        LogEven.logEvent(activity, "rate_show", bundle)
    }

    override fun bindView() {
        binding.apply {
            btnRate.tap {
                if (rtb.rating == 0f) {
                    Toast.makeText(activity, R.string.please_give_rating, Toast.LENGTH_SHORT).show()
                    return@tap
                }
                dismiss()
                if (rtb.rating <= 4) {
                    sendMail()
                } else {
                    sendRate()
                }
                var bundle = Bundle()
                bundle.putFloat("rate_star", rtb.rating)
                LogEven.logEvent(activity, "rate_submit", bundle)
                Toast.makeText(activity, R.string.thank_you_for_rate_us, Toast.LENGTH_SHORT).show()
            }
            // nút later
            btnLater.tap {
                LogEven.logEvent(activity, "rate_not_now", Bundle())
                dismiss()
            }
        }
    }

    private fun sendRate() {
        val manager: ReviewManager = ReviewManagerFactory.create(activity)
        val request: Task<ReviewInfo> = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.getResult()
                val flow: Task<Void> = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnSuccessListener {
                    SharePrefUtils.forceRated(activity)
                    if (isFinishActivity) activity.finishAffinity()
                }
            } else {
                SharePrefUtils.forceRated(activity)
                dismiss()
                if (isFinishActivity) activity.finishAffinity()
            }
        }
    }


    //phương thức gửi mail
    private fun sendMail() {
        val uriText =
            "mailto:${Default.EMAIL}?subject=${Default.SUBJECT}&body=Rate : ${binding.rtb.rating}" + "Content: ".trimIndent()
        val uri = Uri.parse(uriText)
        val sendIntent = Intent(Intent.ACTION_SENDTO)
        sendIntent.data = uri

        try {
            activity.startActivity(Intent.createChooser(sendIntent, "Send mail"))
            SharePrefUtils.forceRated(activity)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(activity, R.string.There_is_no_email, Toast.LENGTH_SHORT).show()
        }
        if (isFinishActivity) activity.finishAffinity()
    }

    //phương thức để thay đổi rating
    private fun changeRating() {
        binding.rtb.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                when (rating) {
                    1f -> {
                        binding.btnRate.text = context.getText(R.string.thank_u)
                        binding.imgIcon.setImageResource(R.drawable.ic_star_1)
                        binding.btnLater.visibility = View.VISIBLE
                    }

                    2f -> {
                        binding.btnRate.text = context.getText(R.string.thank_u)
                        binding.imgIcon.setImageResource(R.drawable.ic_star_2)
                        binding.btnLater.visibility = View.VISIBLE
                    }

                    3f -> {
                        binding.btnRate.text = context.getText(R.string.thank_u)
                        binding.imgIcon.setImageResource(R.drawable.ic_star_3)
                        binding.btnLater.visibility = View.VISIBLE
                    }

                    4f -> {
                        binding.btnRate.text = context.getText(R.string.thank_u)
                        binding.imgIcon.setImageResource(R.drawable.ic_star_4)
                        binding.btnLater.visibility = View.GONE
                    }

                    5f -> {
                        binding.btnRate.text = context.getText(R.string.thank_u)
                        binding.imgIcon.setImageResource(R.drawable.ic_star_5)
                        binding.btnLater.visibility = View.GONE
                    }

                    else -> {
                        binding.btnRate.text = context.getText(R.string.rate)
                        binding.imgIcon.setImageResource(R.drawable.ic_star_0)
                        binding.btnLater.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun loadNative() {
        try {
            if (SharePrefRemote.get_config(
                    activity, SharePrefRemote.native_popup
                ) && AdsConsentManager.getConsentResult(activity)
            ) {

                binding.frAds.visibility = View.VISIBLE
                val nativeBuilder = NativeBuilder(
                    activity,
                    binding.frAds,
                    R.layout.ads_native_large_top_shimer,
                    R.layout.layout_native_large_top,
                    R.layout.layout_native_large_top
                )
                nativeBuilder.setListIdAd(AdmobApi.getInstance().getListIDByName("native_popup"))
                val nativeManager = NativeManager(
                    activity,
                    life,
                    nativeBuilder
                ).setIntervalReloadNative(Contants.interval_reload_native)
            } else {
                binding.frAds.visibility = View.GONE
                binding.frAds.removeAllViews()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            binding.frAds.visibility = View.GONE
            binding.frAds.removeAllViews()
        }
    }

}