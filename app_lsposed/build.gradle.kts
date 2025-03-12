plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
}

private val COMPILE_SDK_VERSION: String by project
private val MIN_SDK_VERSION: String by project

android {
    namespace = "com.liangguo.tools.lsposed"
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
}

dependencies {
    implementation(project(":lib_bu_common"))
//    compileOnly(project(":lib_oem_sdk"))

    compileOnly(libs.xposedApi)
    implementation(libs.ezxhelper)
    implementation(libs.kxposedhelper)
    implementation(libs.yukihookApi)
    ksp(libs.yukihookKsp)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}