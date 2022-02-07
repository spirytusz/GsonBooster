package com.spirytusz.booster.processor.test

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