package com.spirytusz.booster.processor.helper

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.spirytusz.booster.processor.const.GSON
import com.spirytusz.booster.processor.const.TYPE_TOKEN
import com.spirytusz.booster.processor.extensions.asNullable
import com.spirytusz.booster.processor.extensions.parameterizedBy
import com.spirytusz.booster.processor.extensions.toTypeAdapterClassName
import com.squareup.kotlinpoet.*
import javax.annotation.processing.ProcessingEnvironment
import com.spirytusz.booster.annotation.Boost

/**
 * 给定了allBoostAnnotatedClassName后，负责生成TypeAdapterFactory
 */
class TypeAdapterFactoryGenerator(
    private val processingEnv: ProcessingEnvironment
) {

    companion object {
        /**
         * 生成的TypeAdapterFactory的默认canonicalName
         * 先写死
         */
        private val DEFAULT_TYPE_ADAPTER_FACTORY_NAME by lazy {
            ClassName("com.spirytusz.booster", "BoosterTypeAdapterFactory")
        }
    }

    /**
     * 生成TypeAdapterFactory.kt
     *
     * @param allBoostAnnotatedClassName 所有被[Boost]所注解的类
     *
     * @return 生成的TypeAdapterFactory的[ClassName]
     */
    fun generate(
        allBoostAnnotatedClassName: List<ClassName>,
        factoryName: ClassName = DEFAULT_TYPE_ADAPTER_FACTORY_NAME
    ): ClassName {
        FileSpec.get(
            factoryName.packageName,
            generateTypeAdapterFactory(allBoostAnnotatedClassName, factoryName)
        ).writeTo(processingEnv.filer)
        return factoryName
    }

    /**
     * 生成TypeAdapterFactory代码
     *
     * @param allBoostAnnotatedClassName 所有被[Boost]所注解的类
     * @param factoryName 生成TypeAdapterFactory的[ClassName]
     *
     * @return 生成的代码
     */
    private fun generateTypeAdapterFactory(
        allBoostAnnotatedClassName: List<ClassName>,
        factoryName: ClassName
    ): TypeSpec {
        return TypeSpec
            .classBuilder(factoryName)
            .addSuperinterface(TypeAdapterFactory::class.java)
            .addFunction(generateCreateFunc(allBoostAnnotatedClassName))
            .build()
    }

    /**
     * 生成TypeAdapterFactory.create方法
     *
     * @param allBoostAnnotatedClassName 所有被[Boost]所注解的类
     *
     * @return create方法代码
     */
    private fun generateCreateFunc(allBoostAnnotatedClassName: List<ClassName>): FunSpec {
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
        allBoostAnnotatedClassName.forEach {
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
}