plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    `maven-publish-plugin`
}

dependencies {
    compileOnly(Dependencies.gradle_build_tool)
    compileOnly(Dependencies.ksp_gradle_plugin)

    implementation(project(":base:contract"))
    implementation(kotlin("gradle-plugin", version = Versions.kotlin_version))
    implementation(Dependencies.asm)
    implementation(Dependencies.gson)
    implementation(Dependencies.common_io)
}

gradlePlugin {
    plugins.register("boost-aggregation-plugin") {
        id = "com.spirytusz.booster.aggregation"
        implementationClass = "com.spirytusz.aggregation.plugin.BoostAggregationPlugin"
    }
}