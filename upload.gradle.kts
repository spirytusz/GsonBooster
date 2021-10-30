apply(plugin = "maven-publish")
apply(plugin = "signing")

fun loadUploadProperties() {
    val properties = java.util.Properties()
    if (project.rootProject.file("local.properties").exists()) {
        println(">>>>> find local.properties")
        properties.load(java.io.FileInputStream(project.rootProject.file("local.properties")))
    } else {
        println(">>>>> Not find local.properties, use gradle.properties instead")
        properties.load(java.io.FileInputStream(project.rootProject.file("gradle.properties")))
    }
    properties.forEach { k, v ->
        println(">>>>> fetch properties $k=$v")
        project.extra[k.toString()] = v
    }
}

tasks {
    val sourcesJar by tasks.creating(Jar::class) {
        val sourceSets: SourceSetContainer by project

        from(sourceSets["main"].allJava)
        classifier = "sources"
    }
    val javadocJar by tasks.creating(Jar::class) {
        from(tasks["javadoc"])
        classifier = "javadoc"
    }

    artifacts {
        add("archives", sourcesJar)
        add("archives", javadocJar)
    }
}

loadUploadProperties()