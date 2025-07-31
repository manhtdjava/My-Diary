package com.dailyjournal.diaryapp.secretdiary.dialog

import android.app.Activity
import android.media.MediaRecorder
import android.os.Environment
import android.os.Handler
import android.util.Log
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
import com.dailyjournal.diaryapp.secretdiary.databinding.DialogRecordingBinding
import com.dailyjournal.diaryapp.secretdiary.listener.SaveRecorderListener
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote
import com.dailyjournal.diaryapp.secretdiary.utils.helper.Contants
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecordingDialog(
    activity1: Activity,
    private var life: LifecycleOwner,
    private var listener: SaveRecorderListener
) : BaseDialog<DialogRecordingBinding>(activity1, false) {
    private var handler = Handler()
    var runnable: Runnable? = null
    private var countTimer = 0
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath = ""
    private var filename = ""
    override fun getContentView(): DialogRecordingBinding {
        return DialogRecordingBinding.inflate(LayoutInflater.from(activity))
    }

    override fun initView() {
        loadNative()
        binding.tvTime.text = "00:00"
        binding.rippleBackground.startRippleAnimation()
        runnable = Runnable {
            activity.runOnUiThread {
                countTimer++
                binding.tvTime.text = setTimer(countTimer)
            }
            handler.postDelayed((runnable)!!, 1000L)
        }
        handler.postDelayed(runnable as Runnable, 1000L)
        startRecording()
    }

    override fun bindView() {
        binding.apply {
            txtSave.tap {
                val dialog = RecorderSaveDialog(activity, life, listener, audioFilePath, filename)
                dialog.show()
                handler.removeCallbacks((runnable)!!)
                dismiss()
                stopRecording()
            }
            txtCancel.tap {
                cancelRecording()
                dismiss()
            }
        }
    }

    private fun setTimer(i: Int): String {
        val format: String
        val j = (i * 1000).toLong()
        try {
            if (j >= 3600000) {
                try {
                    format = String.format(
                        Locale.getDefault(), "%02d:%02d:%02d", java.lang.Long.valueOf(
                            TimeUnit.MILLISECONDS.toHours(j)
                        ), java.lang.Long.valueOf(
                            TimeUnit.MILLISECONDS.toMinutes(j) % TimeUnit.HOURS.toMinutes(1L)
                        ), java.lang.Long.valueOf(
                            TimeUnit.MILLISECONDS.toSeconds(j) % TimeUnit.MINUTES.toSeconds(1L)
                        )
                    )
                } catch (unused: NumberFormatException) {
                    println(j)
                    return "00:00"
                }
            } else {
                format = String.format(
                    Locale.getDefault(), "%02d:%02d", java.lang.Long.valueOf(
                        TimeUnit.MILLISECONDS.toMinutes(j) % TimeUnit.HOURS.toMinutes(1L)
                    ), java.lang.Long.valueOf(
                        TimeUnit.MILLISECONDS.toSeconds(j) % TimeUnit.MINUTES.toSeconds(1L)
                    )
                )
            }
            return format
        } catch (unused2: Exception) {
            return "00:00"
        }
    }

    private fun startRecording() {
        try {
            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .toString() + File.separator + "MyDiary"
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            var audioFile = "diary_" + SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            filename = audioFile
            audioFilePath = file.absolutePath + File.separator + audioFile + ".m4a"
            val mediaRecorder = MediaRecorder()
            this.mediaRecorder = mediaRecorder
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            this.mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            this.mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            this.mediaRecorder?.setOutputFile(file.absolutePath + File.separator + audioFile + ".m4a")
            this.mediaRecorder?.prepare()
            this.mediaRecorder?.start()
        } catch (e: Exception) {
            Toast.makeText(activity, R.string.error_while_starting_recording, Toast.LENGTH_SHORT)
                .show()
            Log.d("error_while_starting_recording", e.message.toString())
            e.printStackTrace()
        }

    }

    private fun stopRecording() {
        var mediaRecorder: MediaRecorder?
        try {
            if ((this.mediaRecorder.also { mediaRecorder = (it) }) != null) {
                mediaRecorder?.stop()
                this.mediaRecorder?.release()
                this.mediaRecorder = null

                //listener.save(filename, audioFilePath)
            }
        } catch (_: Exception) {
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
