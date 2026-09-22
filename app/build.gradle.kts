plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ffarena.tournaments"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ffarena.tournaments"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    flavorDimensions += "app"

    productFlavors {
        create("player") {
            dimension = "app"
            applicationIdSuffix = ".player"
            versionNameSuffix = "-player"
            resValue("string", "app_name", "FF Arena")
            resValue("string", "start_path", "/")
        }
        create("admin") {
            dimension = "app"
            applicationIdSuffix = ".admin"
            versionNameSuffix = "-admin"
            resValue("string", "app_name", "FF Arena Admin")
            resValue("string", "start_path", "/admin")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
