plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.simplerecipe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.simplerecipe"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.firebase.firestore.ktx)
    val fragment_version =  "1.8.2"
    val lottie_version = "3.4.0"
    val material_version = "1.12.0"
    val epoxy_version = "5.1.4"
    val room_version = "2.6.1"

    //retrofit used to fetch API Calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    //image loading library
    implementation("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    //splash screen
    implementation("androidx.core:core-splashscreen:1.0.0")

    //lottie animation
    implementation("com.airbnb.android:lottie:$lottie_version")

    //material depedencies
    implementation("com.google.android.material:material:$material_version")

    // FirebaseUI for Cloud Firestore
    implementation("com.firebaseui:firebase-ui-firestore:8.0.2")

    //epoxy
    implementation("com.airbnb.android:epoxy:$epoxy_version")
    // Add the annotation processor if you are using Epoxy's annotations (recommended)
    annotationProcessor("com.airbnb.android:epoxy-processor:$epoxy_version")

    // Room
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("androidx.fragment:fragment-ktx:$fragment_version")
}