plugins {
    kotlin("jvm")
    java
    id("org.jetbrains.intellij")
}

group = "com.spirytusz.booster"
version = "1.0-SNAPSHOT"

intellij {
    version.set(Versions.intellij_ide)
    plugins.set(listOf("java", "Kotlin"))
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent())
    }
}

dependencies {
    implementation(kotlin("stdlib"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}