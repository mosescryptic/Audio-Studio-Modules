plugins {
    id("com.android.application")
}

android {
    namespace = "com.mosescryptic.audiostudio.test"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mosescryptic.audiostudio.test"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.16.0")
}
