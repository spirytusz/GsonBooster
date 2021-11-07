package com.spirytusz.booster.processor.gen

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.config.BoosterGenConfig
import com.spirytusz.booster.processor.gen.const.Constants.GSON
import com.spirytusz.booster.processor.gen.const.Constants.TYPE_TOKEN
import com.spirytusz.booster.processor.gen.extension.asTypeName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterClassName
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.writeTo

class BoosterTypeAdapterFactoryGenerator(private val environment: SymbolProcessorEnvironment) {

    /**
     * 生成TypeAdapterFactory.kt
     *
     * @param allBoostAnnotatedClasses 所有被[Boost]所注解的类
     *
     * @return 生成的TypeAdapterFactory的[ClassName]
     */
    fun generate(
        allBoostAnnotatedClasses: Set<AbstractClassScanner>,
        config: BoosterGenConfig
    ): ClassName? {
        val factoryName = resolveTypeAdapterFactoryName(config) ?: return null
        environment.logger.warn(factoryName.toString())
        val fileSpec = FileSpec.builder(factoryName.packageName, factoryName.simpleName)
            .addType(generateTypeAdapterFactory(allBoostAnnotatedClasses, factoryName))
            .build()
        fileSpec.writeTo(codeGenerator = environment.codeGenerator, aggregating = false)
        return factoryName
    }

    private fun resolveTypeAdapterFactoryName(config: BoosterGenConfig): ClassName? {
        if (!config.generateTypeAdapterFactory) {
            return null
        }
        val split = config.typeAdapterFactoryName.split(".")
        if (split.size <= 2) {
            return null
        }
        val packageName = split.subList(0, split.size - 1).joinToString(separator = ".") { it }
        val simpleName = split.last()
        return ClassName(packageName, simpleName)
    }

    private fun generateTypeAdapterFactory(
        allBoostAnnotatedClasses: Set<AbstractClassScanner>,
        factoryName: ClassName
    ): TypeSpec {
        return TypeSpec
            .classBuilder(factoryName)
            .addSuperinterface(TypeAdapterFactory::class)
            .addFunction(generateCreateFunc(allBoostAnnotatedClasses))
            .build()
    }

    private fun generateCreateFunc(allBoostAnnotatedClasses: Set<AbstractClassScanner>): FunSpec {
        val returnType = TypeAdapter::class.asClassName()
            .parameterizedBy(TypeVariableName.invoke("T")).copy(nullable = true)

        val createFun = FunSpec.builder("create")
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S", "UNCHECKED_CAST")
                    .build()
            )
            .addModifiers(KModifier.OVERRIDE)
            .addTypeVariable(TypeVariableName.invoke("T"))
            .addParameter(GSON, Gson::class)
            .addParameter(
                TYPE_TOKEN,
                TypeToken::class.asTypeName().parameterizedBy(TypeVariableName.invoke("T"))
            )
            .returns(returnType)

        val codeBlock = CodeBlock.Builder()
        codeBlock.beginControlFlow("val typeAdapter = when")
        allBoostAnnotatedClasses.forEach {
            codeBlock.addStatement(
                "%T::class.java.isAssignableFrom(%L.rawType) -> %T(%L)",
                it.ksClass.asTypeName(),
                TYPE_TOKEN,
                it.getTypeAdapterClassName(),
                GSON
            )
        }
        codeBlock.addStatement("else -> null")
        codeBlock.endControlFlow()
        codeBlock.addStatement("return typeAdapter as? %L", returnType)
        createFun.addCode(codeBlock.build())
        return createFun.build()
    }
}