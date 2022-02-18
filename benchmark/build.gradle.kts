plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(Dependencies.kotlin_stdlib)

    implementation(Dependencies.gson)

    implementation(Dependencies.jmh)
    implementation(Dependencies.jmh_processor)

    implementation(project(":booster-annotation"))
    kapt(project(":booster-processor:processor-kapt"))
}

kapt {
    arguments {
        arg("factoryName", "com.spirytusz.booster.BoosterTypeAdapterFactory")
    }
}

sourceSets {
    getByName("main").java.srcDir("../buildSrc/src/main/java")
}