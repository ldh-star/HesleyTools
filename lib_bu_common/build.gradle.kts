plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

private val COMPILE_SDK_VERSION: String by project
private val MIN_SDK_VERSION: String by project
private val VERSION_NAME: String by project
private val VERSION_CODE: String by project
private val BASE_APPLICATION_ID: String by project

android {
    namespace = "com.liangguo.lib.common"
    compileSdk = COMPILE_SDK_VERSION.toInt()

    defaultConfig {
        minSdk = MIN_SDK_VERSION.toInt()

        android.buildFeatures.buildConfig = true
        buildConfigField("String", "APPLICATION_ID", BASE_APPLICATION_ID.suite())
        buildConfigField("String", "VERSION_NAME", VERSION_NAME.suite())
        buildConfigField("int", "VERSION_CODE", VERSION_CODE)

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

    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

fun String?.suite() = "\"$this\""
