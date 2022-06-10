plugins {
    kotlin("jvm")
}

dependencies {
    api(Dependencies.junit)
    api(Dependencies.gson)
    api(Dependencies.kotlin_compiler_testing)

    api(project(":booster-annotation"))
}