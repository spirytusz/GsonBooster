package com.spirytusz.aggregation.plugin.task

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.spirytusz.aggregation.plugin.asm.BundleClassGenerator
import org.apache.commons.io.FileUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import java.io.File

@CacheableTask
abstract class GsonBoosterBundleClassGenerateTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val aggregatedJsonFile: RegularFileProperty

    @get:OutputFile
    abstract val bundleKotlinSourceClass: RegularFileProperty

    private val gson = Gson()

    @TaskAction
    fun generateGsonBoosterBundleClass() {
        val inputFile = aggregatedJsonFile.asFile.get()
        val outputFile = bundleKotlinSourceClass.asFile.get()

        project.logger.info("InputFile: $inputFile")
        project.logger.info("OutputFile: $outputFile")

        val jsonObject = inputFile.readAsJson()
        val typeAdapterNames = jsonObject.get("type_adapter_names").asJsonObject.let {
            mutableMapOf<String, String>().apply {
                it.keySet().forEach { key ->
                    put(key, it.get(key).asString)
                }
            }
        }
        val factoryNames = jsonObject.get("type_adapter_factory_names").asJsonArray
            .map { it.asString }

        val codeGenerator = BundleClassGenerator(typeAdapterNames, factoryNames)
        val generatedClassByteArray = codeGenerator.generate()
        outputFile.writeBytes(generatedClassByteArray)
    }

    private fun File.readAsJson(): JsonObject {
        return gson.fromJson(readText(), JsonObject::class.java)
    }
}