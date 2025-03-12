import com.android.build.gradle.internal.api.BaseVariantOutputImpl

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutLibraries)
}

/** 默认签名的keystore */
private val DEFAULT_SIGN_KEY_STORE = "DefaultSignKeyStore"

private val TARGET_SDK_VERSION: String by project
private val COMPILE_SDK_VERSION: String by project
private val MIN_SDK_VERSION: String by project
private val VERSION_NAME: String by project
private val VERSION_CODE: String by project
private val BASE_APPLICATION_ID: String by project

android {
    namespace = BASE_APPLICATION_ID
    compileSdk = COMPILE_SDK_VERSION.toInt()

    defaultConfig {
        applicationId = BASE_APPLICATION_ID
        minSdk = MIN_SDK_VERSION.toInt()
        targetSdk = TARGET_SDK_VERSION.toInt()
        versionCode = VERSION_CODE.toInt()
        versionName = VERSION_NAME

        android.buildFeatures.buildConfig = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/kotlin/**"
            excludes += "/*.txt"
            excludes += "/*.bin"
            excludes += "/*.json"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        dex {
            useLegacyPackaging = true
        }
        applicationVariants.all {
            outputs.all {
                (this as BaseVariantOutputImpl).outputFileName =
                    "${rootProject.name}_${versionName}_${versionCode}_${buildType.name}.apk"
            }
        }
    }

    signingConfigs {
        // debug和release包都共用这一个默认签名配置
        create(DEFAULT_SIGN_KEY_STORE) {
            keyAlias = "key0"
            keyPassword = "263761"
            storeFile = file("../密码263761.jks")
            storePassword = "263761"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        val signConfig = signingConfigs.getByName(DEFAULT_SIGN_KEY_STORE)
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signConfig
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signConfig
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation("com.github.ldh-star:ClarityPermission:1.0.9")
//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.8.1")
    implementation(project(":lib_bu_common"))
    implementation(project(":lib_base_shell"))
    implementation(project(":app_lsposed"))
    implementation("com.jakewharton.picnic:picnic:0.7.0")
    implementation(libs.aboutlibraries.core)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.accompanist.insets)
    implementation(libs.androidx.ui.android)
}

// 给字符串套上“”
fun String?.suite() = "\"$this\""