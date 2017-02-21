# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

##---------------Begin: proguard configuration common for all Android apps ----------
#-optimizations !code/simplification/cast,!field/*,!class/merging/*

-dontpreverify
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-ignorewarnings
# ---- UI ----
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepattributes Signature
-keep class com.google.gson.Gson {
    public *;
}
-keep class com.google.gson.reflect.TypeToken { *; }


##---------------Begin: proguard configuration for google service ----
-keep class com.google.android.gms.play-services-gcm.** { *; }
##---------------End: proguard configuration for google service  ----------

##---------------Begin: proguard configuration for Bugly  ----------
-keep class com.tencent.bugly.** { *; }
-keep public class com.tencent.bugly.crashreport.crash.jni.NativeCrashHandler{public *; native <methods>;}
-keep public interface com.tencent.bugly.crashreport.crash.jni.NativeExceptionHandler{*;}

##---------------End: proguard configuration for Bugly  ----------

##---------------Begin: proguard configuration for MTA  ----------
-keep class com.tencent.stat.** { *; }
-keep class com.tencent.mid.** { *; }
-keep class com.appsflyer.** { *; }
#avazu tracking
-keep class com.avazu.tracking.** { *; }

##---------------End: proguard configuration for MTA  ----------

-keep class com.bluepay.** { *; }

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class dotc.android.happybuy.proguard.NoProguard{*;}
-keep class * implements dotc.android.happybuy.proguard.NoProguard { *; }
-keep class com.example.android.trivialdrivesample.util.**{*;}
-keep class dotc.android.happybuy.http.result.**{*;}
-keep class dotc.android.happybuy.push.Topic{*;}
-keep class dotc.android.happybuy.config.abtest.bean.**{*;}
-keep class com.stat.analytics.bean.** { *; }
-keep class mobi.andrutil.autolog.**{*;}
-keep class mobi.andrutil.cm.**{*;}
-keep interface mobi.andrutil.cm.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule