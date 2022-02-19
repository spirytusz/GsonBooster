plugins {
    kotlin("jvm") version Versions.kotlin_version apply false
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(Dependencies.gradle_build_tool)
        classpath(kotlin("gradle-plugin", version = Versions.kotlin_version))
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}