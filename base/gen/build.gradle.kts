plugins {
    kotlin("jvm")
    kotlin("kapt")
}

apply(from = "../../upload.gradle")

dependencies {
    api(project(":base:processor-base"))

    implementation(Dependencies.auto_service_annotation)
    kapt(Dependencies.auto_service)
}