plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.flowledger"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.flowledger"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    
    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    
    // Lifecycle
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    
    // Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    
    // Dynamic Animation
    implementation(libs.dynamicanimation)
    
    // Charts
    implementation(libs.mpandroidchart)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "androidx.lifecycle") {
            useVersion(libs.versions.lifecycle.get())
        }
        if (requested.group == "androidx.navigation") {
            useVersion(libs.versions.navigation.get())
        }
    }
}