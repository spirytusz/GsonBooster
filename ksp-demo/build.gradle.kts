plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = BuildTools.compileSdkVersion

    defaultConfig {
        applicationId = "com.spirytusz.gsonbooster.ksp"
        minSdk = BuildTools.minSdkVersion
        targetSdk = BuildTools.targetSdkVersion
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            sourceSets.getByName("main") {
                java.srcDir("build/generated/ksp/debug/kotlin")
            }
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            sourceSets.getByName("main") {
                java.srcDir("build/generated/ksp/release/kotlin")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(Dependencies.kotlin_stdlib)
    implementation(Dependencies.androidx_appcompat)
    implementation(Dependencies.material_design)
    implementation(Dependencies.androidx_constraintlayout)
    testImplementation(Dependencies.junit)

    implementation(Dependencies.gson)
    implementation(Dependencies.booster_annotation)
    ksp(Dependencies.booster_processor_ksp)
    ksp(Dependencies.booster_aggregation_processor)
}

ksp {
    arg("factory", "com.spirytusz.booster.ksp.BoosterTypeAdapterFactory")
}
