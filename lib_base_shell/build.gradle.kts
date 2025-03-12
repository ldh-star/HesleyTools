plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

private val COMPILE_SDK_VERSION: String by project
private val MIN_SDK_VERSION: String by project

android {
    namespace = "com.liangguo.libs.shell"
    compileSdk = COMPILE_SDK_VERSION.toInt()

    defaultConfig {
        minSdk = MIN_SDK_VERSION.toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    android {
        buildFeatures {
            aidl = true
        }
    }
}

dependencies {
    implementation(project(":lib_bu_common"))
    implementation("com.github.topjohnwu.libsu:core:6.0.0")
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}