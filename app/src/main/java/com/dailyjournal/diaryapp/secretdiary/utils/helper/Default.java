package com.dailyjournal.diaryapp.secretdiary.utils.helper;

import android.Manifest;
import android.os.Build;

public class Default {
    //about app
    public static final String EMAIL = "";
    public static final String SUBJECT = "Daily Journal: My Secret Diary";
    public static final String PRIVACY_POLICY = "https://amazic.net/Privacy-Policy-Diary.html";
    //Name permission
    public static final String[] STORAGE_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? new String[]{Manifest.permission.READ_MEDIA_IMAGES}
            : new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final String RECORD_PERMISSION = Manifest.permission.RECORD_AUDIO;
    public static final String NOTIFY_PERMISSION = Manifest.permission.POST_NOTIFICATIONS;

}

