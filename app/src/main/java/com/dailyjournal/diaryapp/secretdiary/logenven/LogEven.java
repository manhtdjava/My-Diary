package com.dailyjournal.diaryapp.secretdiary.logenven;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class LogEven {
    public static void logEvent(Context context, String nameEvent, Bundle params) {
        Log.e("AdmobEvent", nameEvent);
        FirebaseAnalytics.getInstance(context).logEvent(nameEvent, params);
    }
}
