package com.spirytusz.booster.processor

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.base.BaseProcessor
import com.spirytusz.booster.processor.const.GSON
import com.spirytusz.booster.processor.const.OBJECT
import com.spirytusz.booster.processor.const.READER
import com.spirytusz.booster.processor.const.WRITER
import com.spirytusz.booster.processor.data.KField
import com.spirytusz.booster.processor.data.type.KType
import com.spirytusz.booster.processor.extensions.asNullable
import com.spirytusz.booster.processor.extensions.kotlinType
import com.spirytusz.booster.processor.extensions.parameterizedBy
import com.spirytusz.booster.processor.extensions.toTypeAdapterClassName
import com.spirytusz.booster.processor.helper.TypeAdapterFactoryGenerator
import com.spirytusz.booster.processor.helper.TypeHelper
import com.spirytusz.booster.processor.strategy.declare.AdapterDeclareStrategy
import com.spirytusz.booster.processor.strategy.read.FieldReadStrategy
import com.spirytusz.booster.processor.strategy.write.FieldWriteStrategy
import com.squareup.kotlinpoet.*
import org.jetbrains.annotations.Nullable
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * Kson注解处理器
 *
 * 负责：
 *     1. 扫描获取所有被Kson注解的类
 *     2. 生成TypeAdapter
 *     3. 生成TypeAdapterFactory
 */
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

    /**
     * 注解处理器的入口，负责扫描和生成TypeAdapter & TypeAdapterFactory 代码
     */
    override fun process(env: RoundEnvironment) {
        log("process start >>>>>>>")
        val start = System.currentTimeMillis()

        // 1. 扫描所有被Kson注解的类
        val ksonAnnotatedClassNames = scanAnnotatedByBoosterClasses(env)
        if (ksonAnnotatedClassNames.isEmpty()) {
            return
        }

        adapterDeclareStrategy.setRegisterTypeAdapters(ksonAnnotatedClassNames)

        // 2. 为所有被Kson注解的类生成TypeAdapter
        val filer = processingEnv.filer
        env.boostAnnotatedClasses.forEach {
            val ksonAnnotatedClass = it.asClassName()
            val adapterSpec = generateTypeAdapter(it)
            FileSpec.get(ksonAnnotatedClass.packageName, adapterSpec).writeTo(filer)
            log("generate TypeAdapter finish >>> ${ksonAnnotatedClass.toTypeAdapterClassName()}")
        }

        // 3. 生成TypeAdapterFactory
        val generatedTypeAdapterFactory = TypeAdapterFactoryGenerator(processingEnv)
            .generate(ksonAnnotatedClassNames.values.toList())
        log("generate TypeAdapterFactory finish >>> $generatedTypeAdapterFactory")
        log("process end >>>>>>> timeCost: [${System.currentTimeMillis() - start}]ms")
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Boost::class.java.name)
    }

    private fun scanAnnotatedByBoosterClasses(env: RoundEnvironment): Map<String, ClassName> {
        return env.boostAnnotatedClasses.map {
            val className = it.asType().asTypeName() as ClassName
            className.canonicalName to className
        }.toMap()
    }

    /**
     * 为指定的类生成TypeAdapter
     *
     * @param clazz 被[Kson]注解的类
     *
     * @return 生成代码所代表的的对象实例
     */
    private fun generateTypeAdapter(clazz: TypeElement): TypeSpec {
        // 1. 扫描该类（不包含父类&接口）的所有变量
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
            .classBuilder(clazz.asClassName().toTypeAdapterClassName())
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

        // 2. 为扫描出来的变量生成TypeAdapter声明语句
        fields.asSequence().distinctBy {
            // 过滤掉重复的type adapter声明
            it.kType.adapterFieldName
        }.mapNotNull {
            // 过滤掉不生成type adapter的类型
            adapterDeclareStrategy.declare(it.kType)
        }.forEach { propertySpec ->
            typeAdapterBuilder.addProperty(propertySpec)
        }

        // 3. 生成read方法，即反序列化方法
        typeAdapterBuilder.addFunction(generateReadFunc(clazz, fields))
        // 4. 生成write方法，即序列化方法
        typeAdapterBuilder.addFunction(generateWriteFunc(clazz, fields))

        return typeAdapterBuilder.build()
    }

    /**
     * 生成指定类的TypeAdapter的read方法
     *
     * @param clazz  被[Kson]注解的类
     * @param fields 该类扫描出来的变量
     */
    private fun generateReadFunc(
        clazz: TypeElement,
        fields: List<KField>
    ): FunSpec {
        // 1. 生成方法签名
        val readFunc = FunSpec.builder("read")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(READER, JsonReader::class.java)
            .returns(clazz.asClassName().asNullable())

        // 2. 生成每个字段的临时变量
        fields.forEach {
            readFunc.addStatement(
                "var ${it.fieldName}: %L = null",
                it.kType.typeName.kotlinType().asNullable()
            )
            if (it.nullable) {
                readFunc.addStatement("var ${it.fetchFlagFieldName} = false")
            }
        }

        // 3. 生成读取json字符串的逻辑
        readFunc.addStatement("$READER.beginObject()")

        readFunc.beginControlFlow("while ($READER.hasNext())")

        readFunc.beginControlFlow("when ($READER.nextName())")
        fields.forEach {
            val conditionCodeBlock = CodeBlock.Builder()
            val keysFormat = it.keys.joinToString(separator = ", ") { "%S" }
            conditionCodeBlock.addStatement("$keysFormat ->", *it.keys.toTypedArray())
            conditionCodeBlock.beginControlFlow("")
            conditionCodeBlock.add(fieldReadStrategy.read(it))
            conditionCodeBlock.endControlFlow()
            readFunc.addCode(conditionCodeBlock.build())
        }
        readFunc.addStatement("else -> $READER.skipValue()")
        readFunc.endControlFlow()

        readFunc.endControlFlow()

        readFunc.addStatement("$READER.endObject()")

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

        // 4. 生成return语句
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

    /**
     * 生成指定类的TypeAdapter的write方法
     *
     * @param clazz  被[Kson]注解的类
     * @param fields 该类扫描出来的变量
     */
    private fun generateWriteFunc(clazz: TypeElement, fields: List<KField>): FunSpec {
        val writeFunc = FunSpec.builder("write")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(WRITER, JsonWriter::class)
            .addParameter(OBJECT, clazz.asClassName().asNullable())

        writeFunc.beginControlFlow("if ($OBJECT == null)")
        writeFunc.addStatement("$WRITER.nullValue()")
        writeFunc.addStatement("return")
        writeFunc.endControlFlow()

        writeFunc.addStatement("$WRITER.beginObject()")
        fields.forEach {
            writeFunc.addCode(fieldWriteStrategy.write(it))
        }
        writeFunc.addStatement("$WRITER.endObject()")

        return writeFunc.build()
    }
}