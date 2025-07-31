package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.listener

import android.net.Uri

interface OnMultiSelectedListener {
    fun onSelected(uriList: List<Uri>)
}