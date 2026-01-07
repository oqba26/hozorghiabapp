plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    // این باید با پکیج نیم اصلی کتابخانه یکی باشد
    namespace = "saman.zamani.persiandate"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    // هماهنگ‌سازی با ماژول اصلی برنامه
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    
    sourceSets {
        getByName("main") {
            java.srcDirs("persiandate/src/main/java")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
}