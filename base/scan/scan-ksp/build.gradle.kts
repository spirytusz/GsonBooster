plugins {
    kotlin("jvm")
    id("maven-publish-plugin")
}

dependencies {
    implementation(Dependencies.kotlin_stdlib)

    api(project(":base:processor-base"))
    implementation(Dependencies.ksp_api)
}