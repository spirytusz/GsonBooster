plugins {
    id("com.android.library")
    id("androidx.benchmark")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdk = BuildTools.compileSdkVersion

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = BuildTools.minSdkVersion
        targetSdk = BuildTools.targetSdkVersion

        testInstrumentationRunner = "androidx.benchmark.junit4.AndroidBenchmarkRunner"
    }

    testBuildType = "release"
    buildTypes {
        getByName("debug") {
            // Since debuggable can"t be modified by gradle for library modules,
            // it must be done in a manifest - see src/androidTest/AndroidManifest.xml
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-proguard-rules.pro"
            )
        }
        getByName("release") {
            isDefault = true
        }
    }
}

dependencies {
    androidTestImplementation(Dependencies.androidx_test_runner)
    androidTestImplementation(Dependencies.androidx_test_junit_ext)
    androidTestImplementation(Dependencies.junit)
    androidTestImplementation(Dependencies.androidx_benchmark_junit)

    // Add your dependencies here. Note that you cannot benchmark code
    // in an app module this way - you will need to move any code you
    // want to benchmark to a library module:
    // https://developer.android.com/studio/projects/android-library#Convert

    implementation(project(":booster-annotation"))
    kapt(project(":booster-processor:processor-kapt"))
    implementation(Dependencies.gson)
}

kapt {
    arguments {
        arg("factory", "com.spirytusz.benchmark.BoosterTypeAdapterFactory")
    }
}