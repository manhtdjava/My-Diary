# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keep class com.dailyjournal.diaryapp.secretdiary.data** { *; }
-keep class com.dailyjournal.diaryapp.secretdiary.adapter** { *; }
-keep class com.dailyjournal.diaryapp.secretdiary.ui** { *; }
-keepattributes Signature

# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations
-keep class com.google.gson.reflect.TypeToken { <fields>; }
-keepclassmembers class **$TypeAdapterFactory { <fields>; }

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    *;
}
-keep public class * implements com.bumptech.glide.load.model.ModelLoader {
    *;
}
-keepclasseswithmembers class * {
    @com.bumptech.glide.annotation.GlideModule *;
}
-keep @com.bumptech.glide.annotation.GlideModule class *

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#-keepclassmembernames class com.dailyjournal.diaryapp.secretdiary.model.PhotoItemModel {<fields>;}
#-keepclassmembernames class com.dailyjournal.diaryapp.secretdiary.model.PhotosModel {<fields>;}
#-keepclassmembernames class com.dailyjournal.diaryapp.secretdiary.model.DiaryItem {<fields>;}


# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**


# google ads
-keep class com.google.android.gms.internal.** { *; }


-keep class com.amazic.ads.** { *; }
-keep class com.dailyjournal.diaryapp.secretdiary.ui.splash.SplashActivity.** { *; }