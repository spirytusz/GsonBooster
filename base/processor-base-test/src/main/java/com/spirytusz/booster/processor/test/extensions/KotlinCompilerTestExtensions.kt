package com.spirytusz.booster.processor.test.extensions

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import java.io.File

fun SourceFile.Companion.fromResource(path: String): SourceFile {
    val file = File(path)
    val name = file.name
    val content = this::class.java.getResource(path).readText()
    return when {
        name.endsWith(".kt") -> kotlin(name, content)
        name.endsWith(".java") -> java(name, content)
        else -> throw IllegalArgumentException("UnSupport file extension $path")
    }
}

fun Any.toJsonObject(): JsonObject {
    val gson = Gson()
    return gson.fromJson(gson.toJson(this), JsonObject::class.java)
}

fun KotlinCompilation.Result.getClassByName(name: String): Class<*> {
    return classLoader.loadClass(name)
}