object Versions {
    const val gradle_build_tool = "4.1.3"

    const val kotlin_version = "1.5.31"
    const val kotlinx_metadata = "0.3.0"

    const val kotlin_poet = "1.10.2"
    const val gson = "2.9.0"
    const val auto_service = "1.0"
    const val kotlin_compiler_testing = "1.4.7"
    const val ksp = "$kotlin_version-1.0.0"
    const val junit = "4.13.2"
    const val jmh = "1.28"
    const val androidx_constraintlayout = "2.1.1"
    const val androidx_appcompat = "1.3.1"
    const val material_design = "1.4.0"

    const val androidx_benchmark_gradle_plugin = "1.0.0"
    const val androidx_test_runner = "1.4.0"
    const val androidx_test_junit_ext = "1.1.3"
    const val androidx_benchmark_junit = "1.0.0"
}

object Dependencies {
    const val gradle_build_tool = "com.android.tools.build:gradle:${Versions.gradle_build_tool}"

    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$${Versions.kotlin_version}"

    const val kotlin_reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_version}"

    const val kotlinx_metadata =
        "org.jetbrains.kotlinx:kotlinx-metadata-jvm:${Versions.kotlinx_metadata}"

    const val kotlin_poet = "com.squareup:kotlinpoet:${Versions.kotlin_poet}"

    const val kotlin_poet_ksp = "com.squareup:kotlinpoet-ksp:${Versions.kotlin_poet}"

    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    const val auto_service_annotation =
        "com.google.auto.service:auto-service-annotations:${Versions.auto_service}"

    const val auto_service = "com.google.auto.service:auto-service:${Versions.auto_service}"

    const val kotlin_compiler_testing =
        "com.github.tschuchortdev:kotlin-compile-testing:${Versions.kotlin_compiler_testing}"

    const val ksp_api = "com.google.devtools.ksp:symbol-processing-api:${Versions.ksp}"

    const val kotlin_compiler_testing_ksp =
        "com.github.tschuchortdev:kotlin-compile-testing-ksp:${Versions.kotlin_compiler_testing}"

    const val junit = "junit:junit:${Versions.junit}"

    const val jmh = "org.openjdk.jmh:jmh-core:${Versions.jmh}"

    const val jmh_processor = "org.openjdk.jmh:jmh-generator-annprocess:${Versions.jmh}"

    const val androidx_constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Versions.androidx_constraintlayout}"

    const val androidx_appcompat = "androidx.appcompat:appcompat:${Versions.androidx_appcompat}"

    const val material_design = "com.google.android.material:material:${Versions.material_design}"

    const val androidx_test_runner = "androidx.test:runner:${Versions.androidx_test_runner}"

    const val androidx_test_junit_ext =
        "androidx.test.ext:junit:${Versions.androidx_test_junit_ext}"

    const val androidx_benchmark_junit =
        "androidx.benchmark:benchmark-junit4:${Versions.androidx_benchmark_junit}"

    const val androidx_benchmark_gradle_plugin =
        "androidx.benchmark:benchmark-gradle-plugin:${Versions.androidx_benchmark_gradle_plugin}"
}