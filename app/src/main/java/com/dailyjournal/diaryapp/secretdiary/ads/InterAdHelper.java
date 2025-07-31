
package com.dailyjournal.diaryapp.secretdiary.ads;

import android.content.Context;
import android.util.Log;

import com.amazic.ads.callback.InterCallback;
import com.amazic.ads.util.Admob;
import com.amazic.ads.util.AdsConsentManager;
import com.dailyjournal.diaryapp.secretdiary.sharePreferent.SharePrefRemote;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.util.List;

public class InterAdHelper {
    private static long lastTimeShowed = 0;
    public static long openAppTime = 0L;

    public long interval_reload_native = 30000L;
    public static void showListInterAd(Context context, boolean config, List<String> id, Runnable onHandle) {
        if (config && canShowNextAdV2(context) && AdsConsentManager.getConsentResult(context)) {

            Admob.getInstance().loadInterAdsFloor(
                    context,
                    id,
                    new InterCallback() {
                        @Override
                        public void onAdLoadSuccess(InterstitialAd interstitialAd) {
                            super.onAdLoadSuccess(interstitialAd);
                            Admob.getInstance().showInterAds(context, interstitialAd, new InterCallback() {
                                @Override
                                public void onNextAction() {
                                    super.onNextAction();
                                    onHandle.run();
                                }

                                @Override
                                public void onAdFailedToShow(AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    onHandle.run();
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError i) {
                            super.onAdFailedToLoad(i);
                            onHandle.run();
                        }
                    });
        } else {
            onHandle.run();
        }
    }
    public static void showInterAd(Context context, boolean config, String id, Runnable onHandle) {
        if (config && canShowNextAdV2(context) && AdsConsentManager.getConsentResult(context)) {

            Admob.getInstance().loadInterAds(
                    context,
                    id,
                    new InterCallback() {
                        @Override
                        public void onAdLoadSuccess(InterstitialAd interstitialAd) {
                            super.onAdLoadSuccess(interstitialAd);
                            Admob.getInstance().showInterAds(context, interstitialAd, new InterCallback() {
                                @Override
                                public void onNextAction() {
                                    super.onNextAction();
                                    onHandle.run();
                                }

                                @Override
                                public void onAdFailedToShow(AdError adError) {
                                    super.onAdFailedToShow(adError);
                                    onHandle.run();
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError i) {
                            super.onAdFailedToLoad(i);
                            onHandle.run();
                        }
                    });
        } else {
            onHandle.run();
        }
    }

    public static boolean canShowNextAd(Context context) {
        boolean a = (System.currentTimeMillis() - lastTimeShowed) >= (SharePrefRemote.get_config_long(context, SharePrefRemote.interval_between_interstitial) * 1000);
        boolean b = System.currentTimeMillis() - openAppTime >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_interstitial_from_start) * 1000;
        if ((System.currentTimeMillis() - lastTimeShowed) >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_between_interstitial) * 1000
                && System.currentTimeMillis() - openAppTime >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_interstitial_from_start) * 1000) {
           // lastTimeShowed = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }
    public static boolean canShowNextAdV2(Context context) {
        boolean a = (System.currentTimeMillis() - lastTimeShowed) >= (SharePrefRemote.get_config_long(context, SharePrefRemote.interval_between_interstitial) * 1000);
        boolean b = System.currentTimeMillis() - openAppTime >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_interstitial_from_start) * 1000;
        if ((System.currentTimeMillis() - lastTimeShowed) >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_between_interstitial) * 1000
                && System.currentTimeMillis() - openAppTime >= SharePrefRemote.get_config_long(context, SharePrefRemote.interval_interstitial_from_start) * 1000) {
            lastTimeShowed = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }


}