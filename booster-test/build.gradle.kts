plugins {
    kotlin("jvm")
    id("kotlin-kapt")
}

sourceSets.test {
    java.srcDirs("$buildDir/generated/source/kapt/test")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    testImplementation("junit:junit:4.13.2")

    implementation("com.google.code.gson:gson:2.8.6")
    implementation(project(":booster-annotation"))
    kapt(project(":booster-processor"))
    kaptTest(project(":booster-processor"))
}