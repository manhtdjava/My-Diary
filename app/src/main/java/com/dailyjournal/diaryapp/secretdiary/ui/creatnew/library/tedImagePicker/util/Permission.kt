package com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.util

import android.Manifest
import android.os.Build
import com.dailyjournal.diaryapp.secretdiary.ui.creatnew.library.tedImagePicker.builder.type.MediaType
import com.gun0912.tedpermission.TedPermissionUtil


internal val MediaType.isPartialAccessGranted: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        && TedPermissionUtil.isGranted(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
        && !TedPermissionUtil.isGranted(*permissions)


internal val MediaType.isFullOrPartialAccessGranted: Boolean
    get() = TedPermissionUtil.isGranted(*permissions) || isPartialAccessGranted
