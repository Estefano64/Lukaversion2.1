plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    //Procesamiento de anotaciones en Kotlin
    id("com.google.devtools.ksp")
    //inyeccion de dependencias
    id("dagger.hilt.android.plugin")
    // Agregar el plugin de Google Services
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.puyodev.luka"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.puyodev.luka"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            // Este bloque se puede dejar vacío o personalizar según sea necesario
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 //1_8 a 17
        targetCompatibility = JavaVersion.VERSION_17 //1_8 a 17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17" //1.8 a 17
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    configurations.all {
        resolutionStrategy {
            force("com.paypal.sdk:paypal-android-sdk:2.16.0")
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/*.kotlin_module"
        }
    }

}

dependencies {
    implementation(libs.play.services.location)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose.v274)//para la navegacion entre pantallas
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.firebase.firestore.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)


    // Agregar Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-perf")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-messaging")

    // Agregar Firebase Analytics (u otros servicios de Firebase que desees)
    implementation("com.google.firebase:firebase-analytics")

    // Dependencias adicionales de Firebase que puedas necesitar

    //inyeccion de dependencias - hilt
    implementation("com.google.dagger:hilt-android:2.47")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    //- compilador
    ksp("com.google.dagger:hilt-compiler:2.47")

    //animacion confetti
    implementation("nl.dionsegijn:konfetti-compose:2.0.4")

    //Dependencia para PayPal
    // PayPal SDK actualizado
   //PayPal Core
    implementation("com.paypal.checkout:android-sdk:1.3.0") {
        exclude(group = "org.jfrog.cardinalcommerce.gradle", module = "cardinalmobilesdk")
    }
    implementation ("com.paypal.android:card-payments:1.1.0")
    implementation("com.paypal.android:payment-buttons:1.1.0")

    // Agregar dependencia para Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Agregar la dependencia de Cardinal Commerce
//    implementation("org.jfrog.cardinalcommerce.gradle:cardinalmobilesdk:2.2.7-5")

    //Para el dialogo de Carga
    implementation("com.valentinilk.shimmer:compose-shimmer:1.2.0")

    // Para manejar JSON
    implementation ("com.squareup.moshi:moshi-kotlin:1.14.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    implementation ("org.json:json:20210307") // Para manejar JSON

    // Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // Lifecycle
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    // Agregar explícitamente la dependencia de Google Play Services para la ubicación
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-location:19.0.1")
}