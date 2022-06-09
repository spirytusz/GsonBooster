plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish-plugin")
}

dependencies {
    api(project(":base:processor-base"))

    implementation(Dependencies.auto_service_annotation)
    kapt(Dependencies.auto_service)
}