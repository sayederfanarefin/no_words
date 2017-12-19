## Google Play Services 4.3.23 specific rules ##
## https://developer.android.com/google/play-services/setup.html#Proguard ##

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keep class com.android.vending.billing.**

 -assumenosideeffects class android.util.Log { *; }
-keep class android.support.v7.widget.SearchView { *; }
-keep class com.github.mikephil.charting.** { *; }
-keep class org.apache.http.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.android.gms.** { *; }
-keep public class com.google.ads.**{  *; }
-dontwarn okio.**
-dontwarn com.fasterxml.jackson.**
-dontwarn java.lang.invoke**
-dontwarn java.lang.invoke*

-dontwarn org.apache.http.**
-dontwarn android.net.**
-dontwarn android.support.v7.**
-dontwarn io.realm.**
 -keep class javax.ws.rs.** { *; }

 -dontwarn retrofit2.Platform$Java8