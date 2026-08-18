# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/admin/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any custom keep rules here:

# Keep JavascriptInterface methods
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep the WebAppInterface class specifically
-keep class com.vibhorejain.road_fighter.WebAppInterface { *; }

# Fix WorkManager / App Startup crash in release builds
-keep class androidx.work.** { *; }
-keep class androidx.startup.** { *; }
-keep class androidx.room.** { *; }

# AdMob specific rules
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
