plugins {
    kotlin("jvm")
    `maven-publish-plugin`
}

dependencies {
    implementation(Dependencies.gson)
    compileOnly(project(":booster-aggregation:runtime-stub"))
}