object Versions {
    val support_lib = "26.1.0"
    val retrofit = "2.4.0"
    val rxjava = "2.1.16"
    val kotlin = "1.2.41"
    val okhttp3 = "3.10.0"
    val butterknife = "8.4.0"
    val greendao = "3.2.2"

    val compileSdkVersion = 26
    val minSdkVersion = 16
    val targetSdkVersion = 26
}

object Libs {
    val support_annotations = "com.android.support:support-annotations:${Versions.support_lib}"
    val support_appcompat_v7 = "com.android.support:appcompat-v7:${Versions.support_lib}"
    val support_v4 = "com.android.support:support-v4:${Versions.support_lib}"
    val support_recyclerview = "com.android.support:recyclerview-v7:${Versions.support_lib}"
    val support_design = "com.android.support:design:${Versions.support_lib}"
    val support_cardview= "com.android.support:cardview-v7:${Versions.support_lib}"

    val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val kotlin_std_lib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_rxjava_adapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    val retrofit_converter_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    val rxjava = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:2.0.2"

    val okhttp3 = "com.squareup.okhttp3:okhttp:${Versions.okhttp3}"
    val okhttp3_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3}"
    val okhttp = "com.squareup.okhttp:okhttp:2.4.0"

    val gson = "com.google.code.gson:gson:2.8.5"
    val eventbus = "org.greenrobot:eventbus:3.1.1"

    val butterknife = "com.jakewharton:butterknife:${Versions.butterknife}"
    val butterknife_compiler = "com.jakewharton:butterknife-compiler:${Versions.butterknife}"
    val butterknife_gradle_plugin = "com.jakewharton:butterknife-gradle-plugin:${Versions.butterknife}"

    val greendao = "org.greenrobot:greendao:${Versions.greendao}"
    val greendao_gradle_plugin = "org.greenrobot:greendao-gradle-plugin:${Versions.greendao}"
    //数据库加密
    val db_sqlcipher = "net.zetetic:android-database-sqlcipher:3.5.7@aar"

    val constraint_layout = "com.android.support.constraint:constraint-layout:1.1.2"

    val photoview = "com.github.chrisbanes:PhotoView:1.2.7"
    //banner 轮播
    val superindicatorlibray = "com.hejunlin.superindicatorlibray:superindicatorlibray:1.0.3"

    val picasso = "com.squareup.picasso:picasso:2.5.2"
    val glide = "com.github.bumptech.glide:glide:3.7.0"

    //状态栏颜色设置 https://github.com/msdx/status-bar-compat
    val status_bar_compat = "com.githang:status-bar-compat:0.7"

    //数据库debug
    val db_debug = "com.amitshekhar.android:debug-db:1.0.1"

    val multidex = "com.android.support:multidex:1.0.3"

    //字体文件加载 https://github.com/chrisjenx/Calligraphy
    val font_calligraphy = "uk.co.chrisjenx:calligraphy:2.3.0"

    val jpush = "cn.jiguang.sdk:jpush:3.0.6"
    val jpush_core = "cn.jiguang.sdk:jcore:1.1.3"

    //内存泄漏检测工具  leakcanary
    val leakcanary_debug = "com.squareup.leakcanary:leakcanary-android-no-op:1.5.4"
    val leakcanary = "com.squareup.leakcanary:leakcanary-android:1.5.4"

    //https://github.com/yanzhenjie/AndPermission 权限管理
    val permission = "com.yanzhenjie:permission:1.1.2"

    //多状态切换 https://github.com/qyxxjd/MultipleStatusView
    val multiple_status_view = "com.classic.common:multiple-status-view:1.4"

    //阿里云OSS
    val ali_oss = "com.aliyun.dpa:oss-android-sdk:2.3.0"

    val zxing = "com.journeyapps:zxing-android-embedded:3.3.0"
}