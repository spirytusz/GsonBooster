plugins {
    kotlin("jvm")
}

apply(from="${project.rootProject.rootDir}/upload.gradle.kts")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")
}