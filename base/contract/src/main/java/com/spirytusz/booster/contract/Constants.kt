package com.spirytusz.booster.contract

object Constants {

    object BoosterKeys {
        const val KEY_TYPE_ADAPTER_FACTORY_NAME = "factory"

        const val KEY_NULL_SAFE = "nullSafe"

        const val KEY_PROJECT_NAME = "project_name"

        const val KEY_IS_MAIN_PROJECT = "is_main_project"
    }

    object PackageNames {
        const val AGGREGATED_PACKAGE_NAME = "com.spirytusz.booster.aggregated"
    }

    object Naming {
        const val TYPE_ADAPTER = "TypeAdapter"

        const val TYPE_ADAPTER_FACTORY = "TypeAdapterFactory"

        const val GSON = "gson"

        const val TYPE = "type"

        const val READER = "reader"

        const val WRITER = "writer"

        const val OBJECT = "obj"

        const val PEEKED = "peeked"

        const val TYPE_TOKEN = "typeToken"

        const val DEFAULT_VALUE = "defaultValue"

        const val RETURN_VALUE = "returnValue"

        const val TEMP_FIELD_PREFIX = "temp"
    }

    object PluginIds {
        const val ANDROID_PLUGIN = "com.android.application"

        const val ANDROID_LIB_PLUGIN = "com.android.library"

        const val KSP_PLUGIN = "com.google.devtools.ksp"

        const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
    }
}