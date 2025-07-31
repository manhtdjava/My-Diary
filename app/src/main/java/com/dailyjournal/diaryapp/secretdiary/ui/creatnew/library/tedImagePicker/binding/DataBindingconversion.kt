package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.binding

import android.view.View
import androidx.databinding.BindingConversion


@BindingConversion
fun convertBooleanToVisibility(visible: Boolean) = if (visible) View.VISIBLE else View.GONE


