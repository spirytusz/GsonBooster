import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `maven-publish-plugin`
}

dependencies {
    implementation(project(":booster-annotation"))
    api(project(":base:contract"))
    implementation(Dependencies.ksp_api)
    implementation(Dependencies.kotlin_stdlib)
    implementation(Dependencies.gson)
    implementation(Dependencies.kotlin_poet)
    implementation(Dependencies.kotlin_poet_ksp)
}

tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}