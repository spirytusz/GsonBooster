plugins {
    kotlin("jvm")
    id("kotlin-kapt")
}

sourceSets.main {
    java.srcDirs("$buildDir/generated/source/kapt")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    implementation("com.google.code.gson:gson:2.8.6")

    implementation("org.openjdk.jmh:jmh-core:1.28")
    implementation("org.openjdk.jmh:jmh-generator-annprocess:1.28")

    implementation(project(":booster-annotation"))
    kapt(project(":booster-processor"))
}