package com.dailyjournal.diaryapp.secretdiary.sharePreferent;


import com.dailyjournal.diaryapp.secretdiary.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.Arrays;
import java.util.List;

public class Common {
    public static void initRemoteConfig(OnCompleteListener listener) {
        FirebaseRemoteConfig.getInstance().reset();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();

        FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(configSettings);
        FirebaseRemoteConfig.getInstance().setDefaultsAsync(R.xml.remote_config_defaults);
        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener(listener);
    }

    public static boolean getRemoteConfigBoolean(String adUnitId) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig.getBoolean(adUnitId);
    }

    public static String getRemoteConfigString(String adUnitId) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig.getString(adUnitId);
    }

    public static Long getRemoteConfigLong(String adUnitId) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        return mFirebaseRemoteConfig.getLong(adUnitId);
    }

    public static List<String> getRemoteConfigListString(String adUnitId) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        String object = mFirebaseRemoteConfig.getString(adUnitId);
        String[] arStr = object.split(",");
        return Arrays.asList(arStr);
    }
}

