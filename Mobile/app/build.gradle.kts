plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ssafy.chelitalk"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ssafy.chelitalk"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.google.android.gms:play-services-auth:20.2.0")

    // 토글
    implementation ("com.github.angads25:toggle:1.1.0")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    // 캐러셀
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.relex:circleindicator:2.1.6")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.6.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:converter-scalars:2.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")


}