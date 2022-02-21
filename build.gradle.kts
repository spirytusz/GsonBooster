plugins {
    id("org.jetbrains.intellij") version Versions.intellij apply false
    kotlin("jvm") version Versions.kotlin
}

allprojects {
    repositories {
        mavenCentral()
    }
}