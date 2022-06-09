import java.util.*

plugins {
    `maven-publish`
    `java-library`
    signing
}

fun getPropertyAsString(key: String): String {
    return ext[key].toString()
}

fun File.readAsProperties(): Properties {
    return reader().use { Properties().apply { load(it) } }
}

ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.secretKeyRingFile"] = null
ext["nexus.username"] = null
ext["nexus.password"] = null

val rootProjectLocalProperties: File = project.rootProject.file("local.properties")
if (rootProjectLocalProperties.exists()) {
    rootProjectLocalProperties.readAsProperties().forEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("KEY_ID")
    ext["signing.password"] = System.getenv("PASSWORD")
    ext["signing.secretKeyRingFile"] = System.getenv("SECRET_KEY_RING_FILE")
    ext["nexus.username"] = System.getenv("SONATYPE_USERNAME")
    ext["nexus.password"] = System.getenv("SONATYPE_PASSWORD")
}

val projectGradleProperties: File = project.file("gradle.properties")
if (!projectGradleProperties.exists()) {
    throw IllegalArgumentException("NOT found gradle.properties in project(${project.name})")
}
val gradleProperties = projectGradleProperties.readAsProperties()

val groupName: String = project.group.toString()
val artifactName: String = gradleProperties.getProperty("MAVEN_ARTIFACT_ID")
val desc: String = gradleProperties.getProperty("MAVEN_POM_DESCRIPTION") ?: ""
val ver: String = project.version.toString()

val username = "ZSpirytus"
val repoUrl = "https://github.com/spirytusz/GsonBooster"
val myEmail = "zhangwel261717@gmail.com"
val licenseName = "MIT License"
val inception = "2022"

java {
    withJavadocJar()
    withSourcesJar()
}

rootProject.subprojects {
    this.group = this.ext["GROUP"].toString()
    this.version = this.ext["VERSION"].toString()
}

publishing {
    publications {
        create<MavenPublication>("pluginMaven") {
            groupId = groupName
            artifactId = artifactName
            version = ver
            from(components["java"])

            pom {
                name.set(artifactName)

                groupId = groupName
                artifactId = artifactName
                version = ver

                description.set(desc)
                url.set(repoUrl)
                inceptionYear.set(inception)

                licenses {
                    license {
                        name.set(licenseName)
                    }
                }
                developers {
                    developer {
                        name.set(username)
                        email.set(myEmail)
                    }
                }
                scm {
                    connection.set("scm:git:git:github.com/spirytusz/GsonBooster.git")
                    developerConnection.set("scm:git:ssh:github.com/spirytusz/GsonBooster.git")
                    url.set(repoUrl)
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = getPropertyAsString("nexus.username")
                password = getPropertyAsString("nexus.password")
            }
        }
    }

    repositories {
        maven {
            name = "myLocal"
            url = uri(rootProject.file(".repo"))
        }
    }
}

signing {
    sign(publishing.publications["pluginMaven"])
}