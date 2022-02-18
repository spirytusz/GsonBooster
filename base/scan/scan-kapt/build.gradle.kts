plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Dependencies.kotlin_stdlib)

    api(project(":base:processor-base"))
    implementation(Dependencies.kotlinx_metadata)
}