plugins {
    kotlin("jvm")
    id("java-gradle-plugin")
    id("com.github.gmazzo.buildconfig")
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

buildConfig {
    packageName.set("com.spirytusz.aggregation.plugin")
    buildConfigField("String", "GSON_BOOSTER_VERSION", "\"$version\"")
}