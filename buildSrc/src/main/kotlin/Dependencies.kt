object Versions {
    const val intellij = "1.4.0"

    const val kotlin_version = "1.5.31"

    const val intellij_ide = "2020.3"

    const val kotlin_poet = "1.10.2"
    const val gson = "2.9.0"
    const val auto_service = "1.0"
}

object Dependencies {
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$${Versions.kotlin_version}"

    const val kotlin_reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin_version}"

    const val kotlin_poet = "com.squareup:kotlinpoet:${Versions.kotlin_poet}"

    const val gson = "com.google.code.gson:gson:${Versions.gson}"

    const val auto_service_annotation =
        "com.google.auto.service:auto-service-annotations:${Versions.auto_service}"

    const val auto_service = "com.google.auto.service:auto-service:${Versions.auto_service}"

}
