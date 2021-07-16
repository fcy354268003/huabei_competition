plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(ProjectProperties.compileSdkVersion)
    buildToolsVersion(ProjectProperties.buildToolsVersion)

    defaultConfig {
        applicationId(ProjectProperties.applicationId)
        minSdkVersion(ProjectProperties.minSdkVersion)
        targetSdkVersion(ProjectProperties.targetSdkVersion)
        versionCode = ProjectProperties.versionCode
        versionName = ProjectProperties.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            moduleName = "native-lib"
        }
        externalNativeBuild {
            cmake {
                abiFilters("armeabi", "armeabi-v7a")//选择要添加的对应cpu类型的.so库。
            }

        }

        manifestPlaceholders(
                mutableMapOf("JPUSH_PKGNAME" to ProjectProperties.applicationId,
                        "JPUSH_APPKEY" to "95a34af8af751fe3af74ec74", //JPush上注册的包名对应的appkey.
                        "JPUSH_CHANNEL" to "developer-default" //暂时填写默认值即可.))
                ))
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        dataBinding = true
    }
    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Libs.kotlinStdlib)
    implementation(Libs.ktxCore)
    implementation(Libs.appCompat)
    implementation(Libs.material)
    implementation(Libs.constraintlayout)
    testImplementation(Libs.testJunit)
    androidTestImplementation(Libs.androidTestJunit)
    androidTestImplementation(Libs.androidTestespressoCore)
    implementation(Libs.glide)
    implementation(Libs.circleimageview)

    implementation(Libs.gson)
    implementation(Libs.retrofit)
    implementation(Libs.retrofit_converter)
    implementation(Libs.cardView)
    implementation(Libs.legacy)
    implementation(Libs.navigationUI)
    implementation(Libs.navigationFragment)
    implementation(Libs.okhttp)
    implementation(Libs.litePal)// Litepal数据库依赖
    implementation(Libs.kawaii)
    implementation(Libs.jmessage)
    implementation(Libs.jcore)
    implementation(Libs.wheelview)// 滑轮滚动选择
    implementation(Libs.barrage)// 弹幕
    implementation(Libs.coroutinue)
    implementation(Libs.coroutineCore)
    implementation(Libs.viewModleKtx)

}