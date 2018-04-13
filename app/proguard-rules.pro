# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /local/tools/adt-bundle-linux-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
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
#指定压缩级别
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

-keep public class com.sun.msv.datatype.SerializationContext { *; }
-keep public class org.relaxng.datatype.ValidationContext { *; }

-dontwarn org.xmlpull.v1.sax2.**
-keep class org.xmlpull.v1.sax2.** { *; }

-dontwarn javax.xml.**
-keep class javax.xml.** { *; }

-dontwarn org.dom4j.** #不要警告找不到org.dom4j.**这个包里面的类的相关引用
-keep class org.dom4j.** { *; } #保持org.dom4j.**这个包里面的所有类和所有方法而不混淆

-dontwarn org.xml.sax.**
-keep class oorg.xml.sax.** { *; }

-dontwarn com.sun.msv.datatype.**
-keep class com.sun.msv.datatype.** { *; }

-dontwarn java.net.**
-keep class java.net.** { *; }

-dontwarn org.jsoup.**
-keep class org.jsoup.** { *; }

-dontwarn com.thq.pat.plugapilib.**
-keep class com.thq.pat.plugapilib.** { *; }


# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**
