import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish-plugin")
}

dependencies {
    implementation(Dependencies.kotlin_stdlib)

    implementation(Dependencies.kotlin_poet)
    implementation(Dependencies.kotlin_poet_ksp)
    implementation(Dependencies.gson)

    implementation(project(":booster-annotation"))
    api(project(":base:processor-base"))
    implementation(project(":base:scan:scan-ksp"))
    runtimeOnly(project(":base:check"))
    runtimeOnly(project(":base:gen"))

    implementation(Dependencies.ksp_api)

    testImplementation(Dependencies.kotlin_compiler_testing_ksp)
    testApi(project(":base:processor-base-test"))
}

tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}