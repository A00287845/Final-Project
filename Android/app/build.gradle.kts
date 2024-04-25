plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.finalandroidmqtt"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.finalandroidmqtt"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.org.eclipse.paho.client.mqttv3)
    implementation (libs.legacy.support.v4)
    implementation (libs.paho.mqtt.android)
    implementation (libs.speedviewlib)
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation (libs.localbroadcastmanager)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation (libs.json)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}