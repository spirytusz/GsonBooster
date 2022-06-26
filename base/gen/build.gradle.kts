plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("maven-publish-plugin")
}

dependencies {
    api(project(":base:processor-base"))
    implementation(project(":booster-annotation"))

    implementation(Dependencies.auto_service_annotation)
    kapt(Dependencies.auto_service)
}