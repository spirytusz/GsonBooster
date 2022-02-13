package com.spirytusz.booster.processor.ksp.test.extensions

import com.tschuchort.compiletesting.KotlinCompilation
import java.io.File

internal val KotlinCompilation.Result.workingDir: File
    get() = outputDirectory.parentFile!!

val KotlinCompilation.Result.kspGeneratedSources: List<File>
    get() {
        val kspWorkingDir = workingDir.resolve("ksp")
        val kspGeneratedDir = kspWorkingDir.resolve("sources")
        val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
        val javaGeneratedDir = kspGeneratedDir.resolve("java")
        val kotlinSources = kotlinGeneratedDir.walkTopDown().filter { it.name.endsWith(".kt") }
        val javaSources = javaGeneratedDir.walkTopDown().filter { it.name.endsWith(".java") }
        return kotlinSources.toList() + javaSources.toList()
    }