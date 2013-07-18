LivePPT-Android
==========================
Android Client for LivePPT

Note that:(2013/7/18)

a. Min-SDK-version: 10 (Android 2.3.3)

b. Android SDK Tools: We recommend Version 22.0 or higher.

c. Original Integrated Development Environments: Android Studio 0.2.x






=================================================================

基于Intellij IDEA / Android Studio开发平台代码部署指南(2013/7/18)

=============================================
【IDE更新至Android Studio 0.2.x】（2013/7/18）

1.从0.1.x版本升级到0.2.x版本前建议先将Android Studio目录下的SDK目录备份，否则安装新版本时会被覆盖，

  另外，根据官方说明，建议把0.1.x卸载并清除目录，而不是把0.2.x安装包安装到原目录进行覆盖，原因是可能会有报错。
  
  【见下官方原文】

2.更新IDE前可以先在File选项导出你的设置，安装完新版本的IDE时重新导入即可【主要针对代码颜色风格的繁琐设置问题】

3.新装的IDE默认SDK目录为Android Studio的根目录\sdk，如果这时候直接导入已有的工程可能会导致出错。
  
  建议：
  
  --覆盖你已经备份的SDK至该目录
  
  --或者随便新建一个工程进入项目后重新配置你的Android SDK Home Path【右击你的工程，选择Open Module Setting-SDKS】


官方提醒原文：

Caution: 

Replacing your existing installation of Android Studio will remove any additional SDK packages you've installed,

such as target platforms, system images, and sample apps. To preserve these, copy them from your current SDK 
        
directory under Android Studio to a temporary location before installing the update. Then move them back once 
         
the update is complete. If you fail to copy these packages, then you can instead download them again through 
         
the Android SDK Manager.


         
Windows users: 

Do not install Android Studio 0.2.x in the same location as 0.1.x.Doing so may cause errors such as 

ClassCastException or other unexpected behaviors. It's best if you remove your previous version of Android Studio 0.1.x.



========================================
请确保你已部署以下环境变量：（2013/7/18）

1.ANDROID_SDK_HOME:在系统环境变量中创建“ANDROID_SDK_HOME”变量，路径为你的android sdk 目录，例如D:\Android_SDK

2.GRADLE_HOME:在系统变量中创建“GRADLE_HOME”变量，路径为你的gradle目录，例如D:\gradle-1.6

3.PATH:在PATH中添加%GRADLE_HOME%\bin;%ANDROID_SDK_HOME%\platform-tools;%ANDROID_SDK_HOME%\tools;



 注意：

 Android Studio 自带了gradle插件目录，但并不完整，第一次创建工程时需要联网下载，由于天朝原因，

 建议直接下载离线文件 http://services.gradle.org/distributions/gradle-1.6-bin.zip ；将它解压

 到你希望的目录即可（当然你可以把它解压到Android Studio的Gradle路径【xx:\Android Studio\plugins\gradle】并覆盖），

 并根据第2点来设置其变量路径。另外，你还需要将下载的离线文件gradle-1.6-bin.zip复制到以下路径

（windows）：C:\Users\<用户名>\.gradle\wrapper\dists\gradle-1.6-bin\72srdo3a5eb3bic159kar72vok 

【复制前请清空本目录】这个目录是联网下载gradle时的文件存放位置，如果你的IDE曾试图联网下载

 gradle，那么本目录下就有残留的不完整的文件，故先删除之。

======================================================================================================
请打开并确保你的Android SDK Manager[SDK Manager.exe]至少达到以下要求，否则请先更新对应内容（2013/7/18）

Tools：

Android SDK Tools ：ver 22.0.1或更高

Android SDK Platform-Tools ：17 或更高

Android SDK Build-Tools : 17或更高

Extras:

Android Support Repository ： 最新

Android Support Library ：最新

其他的API请根据应用程序的版本要求更新

==============================
导入已有工程须知（2013/7/18）：

打开工程目录下的build.gradle,对应的内容检查/修改如下（若符合则无须修改）

classpath 'com.android.tools.build:gradle:0.4.0' 【Android Studio 0.1.x 的旧gradle 版本】
 
改为：
 
classpath 'com.android.tools.build:gradle:0.5.+' 【Android Studio 0.2.x 的新gradle 版本】
 
 
请确保build.gradle中的 buildToolsVersion 版本号和Android SDK Manager中的Android Build-Tools版本一致，比如现在为17
 
即 buildToolsVersion "17"



============================
导入已有工程步骤（2013/7/18）

S1:File-Import Project

S2:选择工程文件所在目录--OK

S3:选择“Create Project From Existing Sources”--Next

S4：“Next”到结束，覆盖提示选择确认。
（完）

注意：不支持Eclipse IDE的直接导入。






                                                     
 
