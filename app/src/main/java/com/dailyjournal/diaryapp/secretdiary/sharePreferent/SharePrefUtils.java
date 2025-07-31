package com.dailyjournal.diaryapp.secretdiary.sharePreferent;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefUtils {
    private static SharedPreferences mPreferences;

    public static void init(Context context) {
        if (mPreferences == null) {
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);
    }

    public static boolean isGoToMain(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("GoToMain", false);
    }

    public static void forceRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("rated", true);
        editor.apply();
    }

    public static void forceGoToMain(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("GoToMain", true);
        editor.apply();
    }


    public static int isFirstTimeLogin(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("FirstLogin", 0);
    }

    public static void setFirstT(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        int currentCount = isFirstTimeLogin(context);
        editor.putInt("FirstLogin", currentCount + 1);
        editor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getString("Password", "");
    }

    public static void setPassword(Context context, String password) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("Password", password);
        editor.apply();
    }

    public static Integer getQuestion(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("Question", 1);
    }

    public static void setQuestion(Context context, Integer type) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("Question", type);
        editor.apply();
    }

    public static String getAnswer(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getString("Answer", "");
    }

    public static void setAnswer(Context context, String answer) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("Answer", answer);
        editor.apply();
    }

    public static String getUser(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getString("User", "");
    }

    public static void setUser(Context context, String answer) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("User", answer);
        editor.apply();
    }

    public static Integer getTheme(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("Theme", 0);
    }

    public static void setTheme(Context context, Integer type) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("Theme", type);
        editor.apply();
    }

    public static boolean isNotify(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("Notify", false);
    }

    public static void setNotify(Context context, boolean isNotify) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("Notify", isNotify);
        editor.apply();
    }

    public static Integer getRepeatNotify(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("RepeatNotify", 0);
    }

    public static void setRepeatNotify(Context context, int isNotify) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("RepeatNotify", isNotify);
        editor.apply();
    }

    public static String getSetTime(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getString("SetTime", "4:00PM");
    }

    public static void setSetTime(Context context, String s) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString("SetTime", s);
        editor.apply();
    }

    public static int getSplashOpenCount(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("splash_open", 1);
    }

    public static void incrementSplashOpenCount(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        int currentCount = getSplashOpenCount(context);
        if (currentCount <= 10) {
            pre.edit().putInt("splash_open", currentCount + 1).apply();
        }
    }

    public static boolean isCheckAdsBG(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsBG", true);
    }

    public static void setCheckAdsBG(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsBG", isCheck);
        editor.apply();
    }

    public static boolean isCheckAdsIMG(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsIMG", false);
    }

    public static void setCheckAdsIMG(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsIMG", isCheck);
        editor.apply();

    }

    public static boolean isCheckAdsTAG(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsTAG", true);
    }

    public static void setCheckAdsTAG(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsTAG", isCheck);
        editor.apply();
    }

    public static boolean isCheckAdsTEXT(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsTEXT", true);
    }

    public static void setCheckAdsTEXT(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsTEXT", isCheck);
        editor.apply();
    }

    public static boolean isCheckAdsRecord(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsRecord", true);
    }

    public static void setCheckAdsRecord(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsRecord", isCheck);
        editor.apply();
    }

    public static boolean isCheckAdsMusic(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsMusic", true);
    }

    public static void setCheckAdsMusic(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsMusic", isCheck);
        editor.apply();
    }
    public static boolean isCheckAdsEMOIJ(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("CheckAdsEMOIJ", true);
    }

    public static void setCheckAdsEMOIJ(Context context, boolean isCheck) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("CheckAdsEMOIJ", isCheck);
        editor.apply();
    }

}