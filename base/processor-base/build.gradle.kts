plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Dependencies.kotlin_stdlib)

    api(Dependencies.kotlin_poet)
    api(Dependencies.kotlin_reflect)
    api(Dependencies.gson)
}