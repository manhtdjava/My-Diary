package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.amazic.ads.service.AdmobApi
import com.amazic.ads.util.AdsConsentManager
import com.amazic.ads.util.manager.native_ad.NativeBuilder
import com.amazic.ads.util.manager.native_ad.NativeManager
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseDialog
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogSaveSoundCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.listener.SaveRecorderListener
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import java.io.File


class RecorderSaveDialog(
    activity1: Activity,
    private var life: LifecycleOwner,
    private var listener: SaveRecorderListener,
    private var audioFilePath: String,
    private var audioFileName: String,
) : BaseDialog<DialogSaveSoundCreatnewBinding>(activity1, false) {
    override fun getContentView(): DialogSaveSoundCreatnewBinding {
        return DialogSaveSoundCreatnewBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
        loadNative()
        binding.edt.setText(File(audioFilePath).nameWithoutExtension)
    }

    override fun bindView() {
        binding.apply {
            txtSave.tap {
                if (binding.edt.text.toString().isNotEmpty()) {
                    renameRecording(binding.edt.text.toString().trim())
                    listener.save(audioFileName, audioFilePath)
                    dismiss()
                } else {
                    Toast.makeText(
                        activity,
                        activity.getString(R.string.title_edit_text_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            txtCancel.tap {
                cancelRecording()
                dismiss()
            }
        }
    }

    private fun renameRecording(newName: String) {
        try {
            val currentFile = File(audioFilePath)
            val newFile = File(currentFile.parent, newName + ".m4a")
            val success = currentFile.renameTo(newFile)
            if (success) {
                audioFilePath = newFile.absolutePath
                audioFileName = newFile.name
            } else {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelRecording() {
        try {
            audioFilePath.let { fileToDelete ->
                val file = File(fileToDelete)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                    activity, life, nativeBuilder
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
