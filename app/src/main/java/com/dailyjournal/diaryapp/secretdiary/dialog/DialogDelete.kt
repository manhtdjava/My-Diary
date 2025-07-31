package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseDialog
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogDeleteSaveBinding
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class DialogDelete(
    activity1: Activity,
    private var life: LifecycleOwner,
    private var action: () -> Unit,
    private var onCancelAction: () -> Unit
) : BaseDialog<DialogDeleteSaveBinding>(activity1, true) {
    override fun getContentView(): DialogDeleteSaveBinding {
        return DialogDeleteSaveBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
    }

    override fun bindView() {
        binding.apply {
            txtGo.tap {
                action.invoke()
                dismiss()
            }
            txtCancel.tap {
                onCancelAction.invoke()
                dismiss()
            }
        }
    }

}