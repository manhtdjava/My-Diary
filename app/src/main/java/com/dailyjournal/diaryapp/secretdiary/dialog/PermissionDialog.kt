package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
import android.view.LayoutInflater
import com.dailyjournal.diaryapp.secretdiary.base.BaseDialog
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogPermissionBinding
import com.dailyjournal.diaryapp.secretdiary.widget.tap


class PermissionDialog(
    activity1: Activity,
    private var action: () -> Unit
) : BaseDialog<DialogPermissionBinding>(activity1, true) {
    override fun getContentView(): DialogPermissionBinding {
        return DialogPermissionBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {

    }

    override fun bindView() {
        binding.apply {
            txtGo.tap {
                action.invoke()
                dismiss()
            }
        }
    }


}