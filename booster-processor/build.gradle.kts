plugins {
    kotlin("jvm")
}

apply(from = "${project.rootProject.rootDir}/upload.gradle.kts")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation("com.squareup:kotlinpoet-ksp:1.10.2")
    implementation("com.google.code.gson:gson:2.8.6")

    implementation(project(":booster-annotation"))

    implementation("com.google.auto.service:auto-service-annotations:1.0-rc5")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview"
}