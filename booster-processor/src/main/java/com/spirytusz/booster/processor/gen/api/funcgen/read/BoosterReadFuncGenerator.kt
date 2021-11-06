package com.spirytusz.booster.processor.gen.api.funcgen.read

import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.stream.JsonReader
import com.spirytusz.booster.processor.config.BoosterGenConfig
import com.spirytusz.booster.processor.gen.api.funcgen.AbstractFunctionGenerator
import com.spirytusz.booster.processor.gen.api.funcgen.read.types.TypeReadCodeGeneratorFactory
import com.spirytusz.booster.processor.gen.const.Constants.DEFAULT_VALUE
import com.spirytusz.booster.processor.gen.const.Constants.READER
import com.spirytusz.booster.processor.gen.const.Constants.RETURN_VALUE
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.toClassName

class BoosterReadFuncGenerator(
    ksFile: KSFile,
    private val config: BoosterGenConfig
) : AbstractFunctionGenerator(ksFile) {

    fun generateReadFunc(classScanner: AbstractClassScanner): FunSpec {
        return FunSpec.builder("read")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(READER, JsonReader::class.java)
            .returns(classScanner.ksClass.toClassName().copy(nullable = true))
            .appendDefaultValueDeclaration(classScanner)
            .appendTempProperties(classScanner)
            .beginObject(READER)
            .appendWhileLoop(classScanner)
            .endObject(READER)
            .appendReturnCodes(classScanner)
            .build()
    }

    private fun FunSpec.Builder.appendDefaultValueDeclaration(
        classScanner: AbstractClassScanner
    ): FunSpec.Builder = this.apply {
        addStatement("val $DEFAULT_VALUE = ${classScanner.ksClass.simpleName.asString()}()")
    }

    private fun FunSpec.Builder.appendTempProperties(
        classScanner: AbstractClassScanner
    ): FunSpec.Builder = this.apply {
        classScanner.allProperties.filterNot {
            it.transient
        }.forEach {
            addStatement("var ${it.fieldName} = $DEFAULT_VALUE.${it.fieldName}")
        }
    }

    private fun FunSpec.Builder.appendWhileLoop(
        classScanner: AbstractClassScanner
    ): FunSpec.Builder = this.apply {
        beginControlFlow("while ($READER.hasNext())")
        beginControlFlow("when ($READER.nextName())")
        classScanner.allProperties.filterNot {
            it.transient
        }.map { propertyDesc ->
            val keys = propertyDesc.keys.ifEmpty { setOf(propertyDesc.fieldName) }.map { "\"$it\"" }
            val keyFormat = keys.joinToString { key -> key }
            CodeBlock.Builder().beginControlFlow("$keyFormat ->", *keys.toTypedArray())
                .apply {
                    TypeReadCodeGeneratorFactory.create(propertyDesc)
                        .generate(this, propertyDesc, config)
                }
                .endControlFlow()
                .build()
        }.forEach {
            addCode(it)
        }
        addStatement("else -> $READER.skipValue()")
        endControlFlow()
        endControlFlow()
    }

    private fun FunSpec.Builder.appendReturnCodes(
        classScanner: AbstractClassScanner
    ): FunSpec.Builder = this.apply {
        val ksClassSimpleName = classScanner.ksClass.simpleName.asString()

        val returnCodeBlock = CodeBlock.Builder()
        val primaryConstructorProperties = classScanner.primaryConstructorProperties.filterNot {
            it.transient
        }.map {
            "${it.fieldName} = ${it.fieldName}"
        }.joinToString { it }
        returnCodeBlock.addStatement(
            "val $RETURN_VALUE = $ksClassSimpleName(%L)",
            primaryConstructorProperties
        )
        classScanner.classProperties.filterNot {
            it.transient
        }.map {
            "$RETURN_VALUE.${it.fieldName} = ${it.fieldName}"
        }.forEach {
            returnCodeBlock.addStatement(it)
        }
        returnCodeBlock.addStatement("return $RETURN_VALUE")

        addCode(returnCodeBlock.build())
    }
}