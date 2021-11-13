plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

sourceSets.test {
    java.srcDir(File("build/generated/ksp/test/kotlin"))
}

ksp {
    arg("factoryName", "com.spirytusz.booster.test.BoostTestTypeAdapterFactory")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    testImplementation("junit:junit:4.13.2")

    implementation("com.google.code.gson:gson:2.8.6")
    implementation(project(":booster-annotation"))
    ksp(project(":booster-processor"))
    kspTest(project(":booster-processor"))
}