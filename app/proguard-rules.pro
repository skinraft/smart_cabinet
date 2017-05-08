# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK/tools/proguard/proguard-android.txt
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
-libraryjars libs/GizWifiSDK.jar
-dontwarn com.gizwits.**
-keep class com.gizwits.**{
    *;
}
-keep class cn.sharesdk.**{*;}
	-keep class com.sina.**{*;}
	-keep class **.R$* {*;}
	-keep class **.R{*;}
	-keep class com.mob.**{*;}
	-dontwarn com.mob.**
	-dontwarn cn.sharesdk.**
	-dontwarn **.R$*

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontoptimize
-verbose
-dontwarn
-dontskipnonpubliclibraryclassmembers
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*
-keepattributes *JavascriptInterface*

-keep public class * extends android.webkit.WebChromeClient
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * implements com.lphtsccft.android.simple.app.ConfigInterface
-keep class com.lphtsccft.android.simple.tool.web.** { *; }
-keep class android.support.v4.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class cn.com.infosec.mobile.android.** { *; }
-keep class cn.microdone.sercurity.** { *; }
-keep class ext.org.bouncycastle.** { *; }
-keep class org.webrtc.videoengine.** { *; }
-keep class org.webrtc.voiceengine.** { *; }
-keep class com.apexsoft.cowork.** { *; }
-keep class com.bairuitech.anychat.** { *; }
-keep class com.lphtsccft.rtdl.share.**{ *; }
-keep class com.lphtsccft.rtdl.palmhall.**{ *; }
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*; }
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*; }
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class ** {
     public void *(***);
 }
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}