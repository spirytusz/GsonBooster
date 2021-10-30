plugins {
    kotlin("jvm")
}

apply(from = "${project.rootProject.rootDir}/upload.gradle.kts")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    implementation("com.squareup:kotlinpoet:1.3.0")
    compileOnly("com.google.code.gson:gson:2.8.6")

    compileOnly(project(":booster-annotation"))

    implementation("com.google.auto.service:auto-service-annotations:1.0-rc5")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.0")
}