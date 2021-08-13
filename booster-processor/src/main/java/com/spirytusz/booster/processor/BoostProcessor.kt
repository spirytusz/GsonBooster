package com.spirytusz.booster.processor

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.squareup.kotlinpoet.*
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.base.BaseProcessor
import com.spirytusz.booster.processor.const.*
import com.spirytusz.booster.processor.const.GSON
import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.BackoffKType
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.extensions.asNullable
import com.spirytusz.booster.processor.extensions.kotlinType
import com.spirytusz.booster.processor.extensions.parameterizedBy
import com.spirytusz.booster.processor.helper.TypeHelper
import com.spirytusz.booster.processor.strategy.declare.AdapterDeclareStrategy
import com.spirytusz.booster.processor.strategy.read.FieldReadStrategy
import com.spirytusz.booster.processor.strategy.write.FieldWriteStrategy
import org.jetbrains.annotations.Nullable
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class BoostProcessor : BaseProcessor() {

    private val adapterDeclareStrategy by lazy {
        AdapterDeclareStrategy()
    }

    private val fieldReadStrategy by lazy {
        FieldReadStrategy()
    }

    private val fieldWriteStrategy by lazy {
        FieldWriteStrategy()
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)

        TypeHelper.init(processingEnv)
    }

    override fun process(env: RoundEnvironment) {
        scanAnnotatedByBoosterClasses(env)

        val filer = processingEnv.filer
        env.boostAnnotatedClasses.forEach {
            val adapterClass = it.asClassName()
            val adapterSpec = generateTypeAdapter(it)
            FileSpec.get(adapterClass.packageName, adapterSpec).writeTo(filer)
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Boost::class.java.name)
    }

    private fun scanAnnotatedByBoosterClasses(env: RoundEnvironment) {
        val registerTypeAdapters: MutableMap<String, ClassName> = mutableMapOf()
        env.boostAnnotatedClasses.forEach {
            val className = it.asType().asTypeName() as ClassName
            val adapterClassName = ClassName(
                className.packageName,
                "${className.simpleName}$TYPE_ADAPTER_NAME"
            )
            registerTypeAdapters[className.canonicalName] = adapterClassName
        }
        adapterDeclareStrategy.setRegisterTypeAdapters(registerTypeAdapters)
    }

    private fun generateTypeAdapter(clazz: TypeElement): TypeSpec {
        log("generateTypeAdapter() >>> $clazz")

        val fields = clazz.enclosedElements.asSequence().filter {
            it.kind == ElementKind.FIELD && !it.modifiers.contains(Modifier.STATIC)
        }.filter {
            !it.modifiers.contains(Modifier.TRANSIENT)
        }.map {
            val serializedName = it.getAnnotation(SerializedName::class.java)
            val alternateKeys = serializedName?.alternate?.toMutableSet() ?: mutableSetOf()
            val keys = alternateKeys.apply {
                val key = serializedName?.value ?: it.simpleName.toString()
                add(key)
            }
            KField(
                keys = keys,
                kType = KType.makeKTypeByTypeName(it.asType().asTypeName()),
                fieldName = it.simpleName.toString(),
                nullable = it.getAnnotation(Nullable::class.java) != null,
                isFinal = it.modifiers.contains(Modifier.FINAL)
            )
        }.toList()

        val typeAdapterBuilder = TypeSpec
            .classBuilder(clazz.asClassName().generateTypeAdapterName())
            .superclass(TypeAdapter::class.parameterizedBy(clazz))
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(GSON, Gson::class.java)
                    .build()
            ).addProperty(
                PropertySpec.builder(GSON, Gson::class)
                    .initializer(GSON)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )

        fields.asSequence().filter {
            // 原始类型不需要typeAdapter
            it.kType.adapterFieldName.isNotEmpty()
        }.distinctBy {
            // 过滤掉重复的type adapter声明
            it.kType.adapterFieldName
        }.mapNotNull {
            // 过滤掉不生成type adapter的类型
            if (it.kType is BackoffKType) {
                log("it.kType.name = ${it.kType.adapterFieldName}")
            }
            adapterDeclareStrategy.declare(it.kType)
        }.forEach { propertySpec ->
            typeAdapterBuilder.addProperty(propertySpec)
        }

        typeAdapterBuilder.addFunction(generateReadFunc(clazz, fields))
        typeAdapterBuilder.addFunction(generateWriteFunc(clazz, fields))

        return typeAdapterBuilder.build()
    }

    private fun generateReadFunc(
        clazz: TypeElement,
        fields: List<KField>
    ): FunSpec {
        val readFunc = FunSpec.builder("read")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(READER, JsonReader::class.java)
            .returns(clazz.asClassName().asNullable())

        fields.forEach {
            readFunc.addStatement(
                "var ${it.fieldName}: %L = null",
                it.kType.typeName.kotlinType().asNullable()
            )
            if (it.nullable) {
                readFunc.addStatement("var ${it.fetchFlagFieldName} = false")
            }
        }

        readFunc.addStatement("%L.beginObject()", READER)

        //region while
        readFunc.beginControlFlow("while (%L.hasNext())", READER)

        //region when
        readFunc.beginControlFlow("when (%L.nextName())", READER)
        fields.forEach {
            val conditionCodeBlock = CodeBlock.Builder()
            val keysFormat = it.keys.joinToString(separator = ", ") { "%S" }
            conditionCodeBlock.addStatement("$keysFormat ->", *it.keys.toTypedArray())
            conditionCodeBlock.beginControlFlow("")
            conditionCodeBlock.add(fieldReadStrategy.read(it))
            conditionCodeBlock.endControlFlow()
            readFunc.addCode(conditionCodeBlock.build())
        }
        readFunc.addStatement("else -> %L.skipValue()", READER)
        readFunc.endControlFlow()
        //endregion when

        readFunc.endControlFlow()
        //endregion while

        readFunc.addStatement("%L.endObject()", READER)

        readFunc.addStatement("val defaultValue = ${clazz.simpleName}()")

        fields.filter {
            it.nullable
        }.forEach {
            val ifCodeBlock = CodeBlock.Builder()
            ifCodeBlock.addStatement(
                """
                val %L = if (%L) {
                    %L
                } else {
                    %L
                }
            """.trimIndent(),
                it.nullableFieldRealFieldName,
                it.fetchFlagFieldName,
                it.fieldName,
                "defaultValue.${it.fieldName}"
            )
            readFunc.addCode(ifCodeBlock.build())
        }

        fun generateReturnStatement(typeName: TypeName, fields: List<KField>): CodeBlock {
            val returnStatement = CodeBlock.Builder()
            returnStatement.addStatement("val returnValue = %T(", typeName)
            // val field
            fields.filter {
                it.isFinal
            }.apply {
                val finalFieldListSize = this.size
                forEachIndexed { index, kField ->
                    val suffix = if (index == finalFieldListSize - 1) {
                        ""
                    } else {
                        ","
                    }
                    if (kField.nullable) {
                        returnStatement.addStatement(
                            "%L = %L%L",
                            kField.fieldName,
                            kField.nullableFieldRealFieldName,
                            suffix
                        )
                    } else {
                        returnStatement.addStatement(
                            "%L = %L ?: %L%L",
                            kField.fieldName,
                            kField.fieldName,
                            "defaultValue.${kField.fieldName}",
                            suffix
                        )
                    }
                }
            }
            returnStatement.addStatement(")")

            // var field
            fields.filterNot {
                it.isFinal
            }.forEach { kField ->
                if (kField.nullable) {
                    returnStatement.beginControlFlow("if (${kField.fetchFlagFieldName})")
                }
                returnStatement.addStatement(
                    "%L.%L = %L ?: %L.%L",
                    "returnValue",
                    kField.fieldName,
                    kField.fieldName,
                    "defaultValue",
                    kField.fieldName
                )
                if (kField.nullable) {
                    returnStatement.endControlFlow()
                }
            }

            returnStatement.addStatement("return returnValue")
            return returnStatement.build()
        }

        readFunc.addCode(generateReturnStatement(clazz.asType().asTypeName(), fields))

        return readFunc.build()
    }

    private fun generateWriteFunc(clazz: TypeElement, fields: List<KField>): FunSpec {
        val writeFunc = FunSpec.builder("write")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(WRITER, JsonWriter::class)
            .addParameter(OBJECT, clazz.asClassName().asNullable())

        writeFunc.beginControlFlow("if (%L == null)", OBJECT)
        writeFunc.addStatement("%L.nullValue()", WRITER)
        writeFunc.addStatement("return")
        writeFunc.endControlFlow()

        writeFunc.addStatement("%L.beginObject()", WRITER)
        fields.forEach {
            writeFunc.addCode(fieldWriteStrategy.write(it))
        }
        writeFunc.addStatement("%L.endObject()", WRITER)

        return writeFunc.build()
    }

    private fun ClassName.generateTypeAdapterName(): String =
        "${simpleName}$TYPE_ADAPTER_NAME"
}