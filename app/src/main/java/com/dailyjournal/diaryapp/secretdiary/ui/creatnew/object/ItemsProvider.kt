package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.`object`

import com.dailyjournal.diaryapp.secretdiary.R
import com.dailyjournal.diaryapp.secretdiary.model.BackGroundModel
import com.dailyjournal.diaryapp.secretdiary.model.ColorTextModel
import com.dailyjournal.diaryapp.secretdiary.model.EmojiModel
import com.dailyjournal.diaryapp.secretdiary.model.FontTextModel

object ItemsProvider {
    fun getDefaultColors(newColor: String? = null): List<ColorTextModel> {
        return listOf(
            ColorTextModel(0, "#4A514F", R.drawable.ic_un_background_text),
            ColorTextModel(1, newColor ?: "#FF7375", R.drawable.ic_text_full_color),
            ColorTextModel(2, "#1D1D1D", R.drawable.ic_shape_textclor2),
            ColorTextModel(3, "#741C10", R.drawable.ic_shape_textclor9),
            ColorTextModel(4, "#FFA2DE", R.drawable.ic_shape_textclor10),
            ColorTextModel(5, "#FF7375", R.drawable.ic_shape_textclor6),
            ColorTextModel(6, "#4A514F", R.drawable.ic_shape_textclor3),
            ColorTextModel(7, "#4B0820", R.drawable.ic_shape_textclor8),
            ColorTextModel(8, "#636363", R.drawable.ic_shape_textclor4),
            ColorTextModel(9, "#464710", R.drawable.ic_shape_textclor5),
            ColorTextModel(10, "#0E2A44", R.drawable.ic_shape_textclor7),
        )
    }

    fun getDefaultEmoji(): List<EmojiModel> {
        return listOf(
            EmojiModel(0, R.drawable.emotion1),
            EmojiModel(1, R.drawable.emotion2),
            EmojiModel(2, R.drawable.emotion3),
            EmojiModel(3, R.drawable.emotion4),
            EmojiModel(4, R.drawable.emotion5),
            EmojiModel(5, R.drawable.emotion6),
            EmojiModel(6, R.drawable.emotion7),
            EmojiModel(7, R.drawable.emotion8),
            EmojiModel(8, R.drawable.emotion9)
        )
    }

    fun getDefaultBack(): List<BackGroundModel> {
        return listOf(
            BackGroundModel(0, R.drawable.bg_background_0),
            BackGroundModel(1, R.drawable.bg_background_1,null),
            BackGroundModel(2, R.drawable.bg_background_2,null),
            BackGroundModel(3, R.drawable.bg_background_3,null),
            BackGroundModel(4, R.drawable.bg_background_4,null),
            BackGroundModel(5, R.drawable.bg_background_5,null),
            BackGroundModel(6, R.drawable.bg_background_6,null),
            BackGroundModel(7, R.drawable.bg_background_7,null),
            BackGroundModel(8, R.drawable.bg_background_8,null),
            BackGroundModel(9, R.drawable.bg_background_9,null),
            BackGroundModel(10, R.drawable.bg_background_10,null),
            BackGroundModel(11, R.drawable.bg_background_11,null),
            BackGroundModel(12, R.drawable.bg_background_12,null),
            BackGroundModel(13, R.drawable.bg_background_13,null),
            BackGroundModel(14, R.drawable.bg_background_14,null),
            BackGroundModel(15, R.drawable.bg_background_15,null),
            BackGroundModel(16, R.drawable.bg_background_16,null),
            BackGroundModel(17, R.drawable.bg_background_17,null),
            BackGroundModel(18, R.drawable.bg_background_18,null),
            BackGroundModel(19, R.drawable.bg_background_19,null),
            BackGroundModel(20, R.drawable.bg_background_20,null),
            BackGroundModel(21, R.drawable.bg_background_21,null),
            BackGroundModel(22, R.drawable.bg_background_22,null),
            BackGroundModel(23, R.drawable.bg_background_23,null),

        )
    }

    fun getDefaultFont(): List<FontTextModel> {
        return listOf(
            FontTextModel(0, "lowanoldstown_roman", R.drawable.ic_lowan_old_style),
            FontTextModel(1, "lemonada_light", R.drawable.ic_lemonada),
            FontTextModel(2, "marckscript_regular", R.drawable.ic_marck_script),
            FontTextModel(3, "plus_jakarta_sans_400", R.drawable.ic_plus_jakrta_sans),
            FontTextModel(4, "palmcanyondrive_regular", R.drawable.ic_palm_canyon_drive),
            FontTextModel(5, "maname_regular", R.drawable.ic_maname),
        )
    }
}
