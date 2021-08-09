package com.zspirytus.booster.processor

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.squareup.kotlinpoet.*
import com.zspirytus.booster.annotation.Boost
import com.zspirytus.booster.annotation.BoostFactory
import com.zspirytus.booster.processor.base.BaseProcessor
import com.zspirytus.booster.processor.const.GSON
import com.zspirytus.booster.processor.const.TYPE_ADAPTER_FACTORY_PREFIX
import com.zspirytus.booster.processor.const.TYPE_ADAPTER_NAME
import com.zspirytus.booster.processor.const.TYPE_TOKEN
import com.zspirytus.booster.processor.extensions.asNullable
import com.zspirytus.booster.processor.extensions.parameterizedBy
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment

@AutoService(Processor::class)
class BoostFactoryProcessor : BaseProcessor() {
    override fun process(env: RoundEnvironment) {
        val allTypeAdapters = findAllTypeAdapter(env)
        if (allTypeAdapters.isEmpty()) {
            return
        }
        val factories = env.boostFactoryAnnotatedClasses.toList()
        val factory = if (factories.size != 1) {
            throw IllegalStateException("The number of classes annotated by @BoostFactory is not 1 $factories")
        } else {
            factories.first().asType().asTypeName() as ClassName
        }
        FileSpec.get(factory.packageName, generateTypeAdapterFactory(factory, allTypeAdapters))
            .writeTo(processingEnv.filer)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(BoostFactory::class.java.name, Boost::class.java.name)
    }

    private fun generateTypeAdapterFactory(factory: ClassName, allTypeAdapters: List<ClassName>): TypeSpec {
        return TypeSpec
            .classBuilder(factory.toTypeAdapterFactoryClassName())
            .addSuperinterface(TypeAdapterFactory::class.java)
            .addFunction(generateCreateFunc(allTypeAdapters))
            .build()
    }

    private fun generateCreateFunc(allTypeAdapters: List<ClassName>): FunSpec {
        val returnType = TypeAdapter::class.parameterizedBy(TypeVariableName.invoke("T"))
        val createFun = FunSpec.builder("create")
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class.java)
                    .addMember("%S", "UNCHECKED_CAST")
                    .build()
            )
            .addModifiers(KModifier.OVERRIDE)
            .addTypeVariable(TypeVariableName.invoke("T"))
            .addParameter(GSON, Gson::class)
            .addParameter(
                TYPE_TOKEN,
                TypeToken::class.parameterizedBy(TypeVariableName.invoke("T"))
            )
            .returns(returnType.asNullable())

        val codeBlock = CodeBlock.Builder()
        codeBlock.beginControlFlow("val typeAdapter = when")
        allTypeAdapters.forEach {
            codeBlock.addStatement(
                "%T::class.java.isAssignableFrom(%L.rawType) -> %T(%L)",
                it,
                TYPE_TOKEN,
                it.toTypeAdapterClassName(),
                GSON
            )
        }
        codeBlock.addStatement("else -> null")
        codeBlock.endControlFlow()
        codeBlock.addStatement("return typeAdapter as? %L", returnType)
        createFun.addCode(codeBlock.build())
        return createFun.build()
    }

    private fun findAllTypeAdapter(env: RoundEnvironment): List<ClassName> {
        return env.boostAnnotatedClasses.map {
            it.asClassName()
        }.toList()
    }

    private fun ClassName.toTypeAdapterClassName() =
        ClassName(this.packageName, "${this.simpleName}$TYPE_ADAPTER_NAME")

    private fun ClassName.toTypeAdapterFactoryClassName() =
        ClassName(this.packageName, "${TYPE_ADAPTER_FACTORY_PREFIX}${this.simpleName}")
}