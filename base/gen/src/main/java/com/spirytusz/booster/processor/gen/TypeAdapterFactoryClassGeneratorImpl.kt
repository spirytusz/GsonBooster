package com.spirytusz.booster.processor.gen

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.gen.TypeAdapterFactoryGenerator
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.gen.const.Const.Naming.GSON
import com.spirytusz.booster.processor.gen.const.Const.Naming.TYPE_TOKEN
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class TypeAdapterFactoryClassGeneratorImpl(
    private val logger: MessageLogger
) : TypeAdapterFactoryGenerator {
    override fun generate(
        typeAdapterFactoryName: String,
        classToTypeAdapters: Set<Pair<KtType, KtType>>
    ): TypeSpec {
        val typeAdapterFactoryClassName = ClassName.bestGuess(typeAdapterFactoryName)
        return TypeSpec
            .classBuilder(typeAdapterFactoryClassName)
            .addSuperinterface(TypeAdapterFactory::class)
            .addFunction(generateCreateFunc(classToTypeAdapters))
            .build()
    }

    private fun generateCreateFunc(classToTypeAdapters: Set<Pair<KtType, KtType>>): FunSpec {
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
            ).returns(returnType)

        val codeBlock = CodeBlock.Builder()
        codeBlock.beginControlFlow("val typeAdapter = when")
        classToTypeAdapters.forEach { (clazz, typeAdapterClazz) ->
            codeBlock.addStatement(
                "%T::class.java.isAssignableFrom(%L.rawType) -> %T(%L)",
                clazz.asTypeName(),
                TYPE_TOKEN,
                typeAdapterClazz.asTypeName(),
                GSON
            )
        }

        codeBlock.addStatement("else -> null")
        codeBlock.endControlFlow()
        codeBlock.addStatement("return typeAdapter as? %T", returnType)
        createFun.addCode(codeBlock.build())
        return createFun.build()
    }
}