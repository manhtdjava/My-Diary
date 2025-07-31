package com.dailyjournal.diaryapp.secretdiary.sharePreferent;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.amazic.ads.util.AdsConsentManager;

public class SharePrefRemote {
    public static String show_ads = "show_ads";
    public static String banner_splash = "banner_splash";
    public static String open_splash = "open_splash";
    public static String inter_splash = "inter_splash";
    public static String native_language = "native_language";
    public static String native_intro = "native_intro";
    public static String native_intro_full = "native_intro_full";
    public static String native_intro_full2 = "native_intro_full2";
    public static String inter_intro = "inter_intro";
    public static String native_permission = "native_permission";
    public static String inter_permission = "inter_permission";
    public static String native_welcome = "native_welcome";
    public static String inter_welcome = "inter_welcome";
    public static String native_password = "native_password";
    public static String collapse_banner_password = "collapse_banner_password";
    public static String inter_password = "inter_password";
    public static String appopen_resume = "appopen_resume";
    public static String native_resume = "native_resume";

    public static String banner_all = "banner_all";
    public static String native_popup = "native_popup";
    public static String native_mine = "native_mine";
    public static String inter_mine = "inter_mine";
    public static String collapse_banner_home = "collapse_banner_home";
    public static String collapse_banner_edit = "collapse_banner_edit";
    public static String native_home = "native_home";
    public static String inter_create = "inter_create";
    public static String inter_preview = "inter_preview";
    public static String native_view_edit = "native_view_edit";
    public static String rewarded_edit = "rewarded_edit";
    public static String rewarded_sound_photo = "rewarded_sound_photo";
    public static String inter_save = "inter_save";
    public static String inter_libruary = "inter_libruary";
    public static String native_libruary = "native_libruary";

    public static String interval_interstitial_from_start = "interval_interstitial_from_start";
    public static String interval_reload_native = "interval_reload_native";
    public static String collap_reload_interval = "collap_reload_interval";
    public static String rate_aoa_inter_splash  = "rate_aoa_inter_splash";
    public static String interval_between_interstitial = "interval_between_interstitial";

    public static boolean get_config(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        if (name_config.equals("style_screen"))
            return pre.getBoolean(name_config, false);
        else
            return pre.getBoolean(name_config, true) && AdsConsentManager.getConsentResult(context) && haveNetworkConnection(context);
    }

    public static void set_config(Context context, String name_config, boolean config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean(name_config, config);
        editor.apply();
    }


    public static String get_config_string(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        return pre.getString(name_config, "");
    }

    public static void set_config_string(Context context, String name_config, String config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putString(name_config, config);
        editor.apply();
    }

    public static Long get_config_long(Context context, String name_config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        return pre.getLong(name_config, 0);
    }

    public static void set_config_long(Context context, String name_config, Long config) {
        SharedPreferences pre = context.getSharedPreferences("remote_fill", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putLong(name_config, config);
        editor.apply();
    }

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}