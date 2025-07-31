package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.base.BaseAdapter
import com.dailyjournal.diaryapp.secretdiary.databinding.ItemSoundRecordCreatnewBinding
import com.dailyjournal.diaryapp.secretdiary.model.ColorTextModel
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel
import com.dailyjournal.diaryapp.secretdiary.model.PhotosModel
import com.dailyjournal.diaryapp.secretdiary.model.SoundsModel
import com.dailyjournal.diaryapp.secretdiary.widget.tap
import java.io.File
import java.util.Locale

class RecordCreatNewAdapter(
    val context: Context,
    private val onItemClick: (Int) -> Unit,
    private val onPlaySound: (Int) -> Unit,
    private val onStopSound: (Int) -> Unit,
    private val onSeekBarChange: (Int, Int) -> Unit,
) : BaseAdapter<ItemSoundRecordCreatnewBinding, SoundsModel>() {
    private var selectedPosition: Int = -1
    private var currentPlayingPosition: Int = -1
    private var mode: String? = null


    @SuppressLint("NotifyDataSetChanged")
    fun setData(newSoundsList: List<SoundsModel>) {
        Log.d("newSoundsList", newSoundsList.size.toString())
        listData.clear()
        listData.addAll(newSoundsList)
        notifyDataSetChanged()
    }
    fun getAllSounds(): List<SoundsModel> {
        return listData
    }
    fun updateSeekBar(position: Int, progress: Int, duration: Int) {
        if (position == currentPlayingPosition) {
            listData[position].apply {
                this.progress = progress
                this.duration = duration.toLong()
            }
            notifyItemChanged(position, "progress")
        }
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemSoundRecordCreatnewBinding =
        ItemSoundRecordCreatnewBinding.inflate(inflater, parent, false)

    override fun creatVH(binding: ItemSoundRecordCreatnewBinding): RecyclerView.ViewHolder =
        SoundVH(binding)

    inner class SoundVH(binding: ItemSoundRecordCreatnewBinding) : BaseVH<SoundsModel>(binding) {
        override fun bind(data: SoundsModel) {
            super.bind(data)
            try {
                binding.txtName.text = data.fileName
                binding.timeEnd.text = getReadableDuration(data.duration)

                binding.seekbar.max = data.duration.toInt()
                Log.d("progress", data.duration.toInt().toString())
                binding.seekbar.progress = data.progress
                Log.d("progress", data.progress.toString())
                binding.timePlay.text = getReadableDuration(data.progress.toLong())

                binding.ivStatus.setImageResource(
                    if (data.isPlay && currentPlayingPosition == adapterPosition) {
                        R.drawable.ic_play_sound
                    } else {
                        R.drawable.ic_stop_sound
                    }
                )

                binding.ivStatus.tap {
                    val previousPosition = currentPlayingPosition

                    if (!data.isPlay) {
                        if (previousPosition >= 0 && previousPosition < listData.size) {
                            listData[previousPosition].isPlay = false
                            notifyItemChanged(previousPosition)
                        }
                        currentPlayingPosition = adapterPosition
                        data.isPlay = true
                        onPlaySound(adapterPosition)
                    } else {
                        currentPlayingPosition = -1
                        data.isPlay = false
                        onStopSound(adapterPosition)
                    }
                    notifyItemChanged(adapterPosition)
                }


                binding.seekbar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser && currentPlayingPosition == adapterPosition) {
                            val readableDuration = getReadableDuration(progress.toLong())
                            binding.timePlay.text = readableDuration
                            data.progress = progress
                            Log.d("progress", progress.toString())
                            onSeekBarChange(adapterPosition, progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        Log.d("SeekBar", "onStartTrackingTouch")
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        Log.d("SeekBar", "onStopTrackingTouch")

                    }

                })

                if (mode == "getTable") {
                    binding.ivDelete.visibility = View.GONE
                    val params = binding.ll1.layoutParams as RelativeLayout.LayoutParams
                    params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
                    binding.ll1.layoutParams = params
                } else {
                    binding.ivDelete.visibility = View.VISIBLE
                    binding.ivDelete.tap {
                        if (currentPlayingPosition == adapterPosition) {
                            currentPlayingPosition = -1
                        }
                        removeRecording(data.filePath)
                        onItemClick(adapterPosition)
                    }
                }

                binding.root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClickListener(data)
                }

            } catch (_: Exception) {
            }
        }
    }


    private fun removeRecording(audioFilePath: String) {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                val file = File(audioFilePath)
                if (file.exists() && file.delete()) {
                    Log.d("SoundAdapter", "File deleted successfully.")
                } else {
                    Log.e("SoundAdapter", "Failed to delete file.")
                }
            }, 500)

        } catch (e: Exception) {
            Log.e("SoundAdapter", "Error while deleting file: ${e.message}")
            e.printStackTrace()
        }
    }

    fun getReadableDuration(j: Long): String? {
        val j2 = j / 1000
        val j3 = j2 / 60
        val j4 = j2 % 60

        return String.format(Locale.getDefault(), "%02d:%02d", j3, j4)
    }
    fun setMode(activityMode: String) {
        mode = activityMode
        notifyDataSetChanged()
    }

}
