plugins {
    kotlin("jvm")
    kotlin("kapt")
}

apply(from = "../../upload.gradle.kts")

dependencies {
    implementation(Dependencies.kotlin_stdlib)

    implementation(Dependencies.kotlin_poet)
    compileOnly(Dependencies.gson)

    implementation(project(":booster-annotation"))
    api(project(":base:processor-base"))
    implementation(project(":base:scan:scan-kapt"))
    implementation(project(":base:check"))
    implementation(project(":base:gen"))

    implementation(Dependencies.auto_service_annotation)
    kapt(Dependencies.auto_service)

    testImplementation(Dependencies.kotlin_compiler_testing)
    testApi(project(":base:processor-base-test"))
}