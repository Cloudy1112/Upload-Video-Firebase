plugins {
    //alias(libs.plugins.android.application)
    id("com.android.application")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.apifirebase"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.apifirebase"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    // Firebase BOM (Quản lý phiên bản)
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    // Thư viện cần thiết
    implementation("com.google.firebase:firebase-auth") // Đăng nhập/Đăng ký
    implementation("com.google.android.material:material") // Để dùng UI đẹp hơn
    implementation("com.google.firebase:firebase-database") // Realtime DB (Lưu thông tin video)
    implementation("com.google.firebase:firebase-storage") // Lưu file Video
    implementation("com.github.bumptech.glide:glide:4.16.0") // Load ảnh/video thumb
    implementation("androidx.viewpager2:viewpager2:1.0.0") // Vuốt video như TikTok
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}