package com.spirytusz.aggregation.plugin.asm

import com.spirytusz.aggregation.plugin.asm.extensions.writeNoArgsConstructor
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_ARRAY_LIST
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_LIST
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_OBJECT
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_TYPE_ADAPTER_FACTORY
import org.objectweb.asm.*
import java.io.File

class BundleClassGenerator(
    private val typeAdapterNames: Map<String, String>,
    private val typeAdapterFactoryNames: List<String>
) {

    companion object {
        private val GENERATE_CLASS_DIRECTORY = "com/spirytusz/booster/aggregation/runtime"
            .replace("/", File.separator)

        private const val FACTORY_FIELD_NAME = "factories"
        private const val REGISTRY_NAME = "BoosterAggregationRegistry"

        private val GENERATED_CLASS =
            "$GENERATE_CLASS_DIRECTORY${File.separator}$REGISTRY_NAME"
    }

    private var currentLineNumber = 0

    fun generate(): List<Pair<String, ByteArray>> {
        currentLineNumber = 0
        val result = mutableListOf<Pair<String, ByteArray>>()
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER,
            GENERATED_CLASS,
            null,
            CLASSNAME_OBJECT,
            arrayOf()
        )
        classWriter.visitField(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL or Opcodes.ACC_STATIC,
            FACTORY_FIELD_NAME,
            "L$CLASSNAME_LIST;",
            "L$CLASSNAME_LIST<L$CLASSNAME_TYPE_ADAPTER_FACTORY;>;",
            null
        ).visitEnd()
        classWriter.visitConstructor()
        classWriter.visitStaticBlock()
        classWriter.generateGetters()
        classWriter.visitEnd()
        result.add("$GENERATED_CLASS.class" to classWriter.toByteArray())

        result.addAll(generateInnerClasses())

        return result
    }

    private fun ClassWriter.visitConstructor() {
        writeNoArgsConstructor(lineNumber = 9)
    }

    private fun ClassVisitor.visitStaticBlock() {

        fun MethodVisitor.initStaticField(
            fieldName: String,
            interfaceType: String,
            realType: String
        ) {
            val label = Label()
            visitLabel(label)
            visitLineNumber(currentLineNumber++, label)
            visitTypeInsn(Opcodes.NEW, realType)
            visitInsn(Opcodes.DUP)
            visitMethodInsn(Opcodes.INVOKESPECIAL, realType, "<init>", "()V", false)
            visitFieldInsn(Opcodes.PUTSTATIC, GENERATED_CLASS, fieldName, "L$interfaceType;")
        }

        fun MethodVisitor.putTypeAdapterFactories() {
            currentLineNumber++

            typeAdapterFactoryNames.forEach { typeAdapterFactoryName ->
                val formattedName = typeAdapterFactoryName.replace(".", "/")
                val label = Label()
                visitLabel(label)
                visitLineNumber(currentLineNumber++, label)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    GENERATED_CLASS,
                    FACTORY_FIELD_NAME,
                    "L$CLASSNAME_LIST;"
                )
                visitTypeInsn(Opcodes.NEW, formattedName)
                visitInsn(Opcodes.DUP)
                visitMethodInsn(Opcodes.INVOKESPECIAL, formattedName, "<init>", "()V", false)
                visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    CLASSNAME_LIST,
                    "add",
                    "(Ljava/lang/Object;)Z",
                    true
                )
                visitInsn(Opcodes.POP)
            }
        }

        fun MethodVisitor.putWrapTypeAdapterFactories() {
            currentLineNumber++

            typeAdapterNames.entries.forEach { (_, typeAdapter) ->
                val wrappedInnerClassName = "${GENERATED_CLASS}_${typeAdapter.getSimpleName()}"
                val label = Label()
                visitLabel(label)
                visitLineNumber(currentLineNumber++, label)
                visitFieldInsn(
                    Opcodes.GETSTATIC,
                    GENERATED_CLASS,
                    FACTORY_FIELD_NAME,
                    "L$CLASSNAME_LIST;"
                )
                visitTypeInsn(Opcodes.NEW, wrappedInnerClassName)
                visitInsn(Opcodes.DUP)
                visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    wrappedInnerClassName,
                    "<init>",
                    "()V",
                    false
                )
                visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    CLASSNAME_LIST,
                    "add",
                    "(L$CLASSNAME_OBJECT;)Z",
                    true
                )
                visitInsn(Opcodes.POP)
            }
        }

        val methodVisitor = visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
        methodVisitor.visitCode()

        currentLineNumber = 11

        methodVisitor.initStaticField(FACTORY_FIELD_NAME, CLASSNAME_LIST, CLASSNAME_ARRAY_LIST)
        currentLineNumber += 2

        methodVisitor.putTypeAdapterFactories()
        methodVisitor.putWrapTypeAdapterFactories()

        val label = Label()
        methodVisitor.visitLabel(label)
        methodVisitor.visitLineNumber(currentLineNumber++, label)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(-1, -1)
        methodVisitor.visitEnd()
    }

    private fun ClassVisitor.generateGetters() {

        fun generateGetter(fieldName: String, desc: String, signature: String) {
            val methodVisitor = visitMethod(
                Opcodes.ACC_PROTECTED or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL,
                "get${fieldName.replaceFirstChar { it.uppercaseChar() }}",
                "()$desc",
                signature,
                null
            )
            methodVisitor.visitCode()
            val label = Label()
            methodVisitor.visitLabel(label)
            methodVisitor.visitLineNumber(currentLineNumber++, label)
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, GENERATED_CLASS, fieldName, desc)
            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitMaxs(-1, -1)
            methodVisitor.visitEnd()
        }

        currentLineNumber += 3
        generateGetter(
            FACTORY_FIELD_NAME,
            "L$CLASSNAME_LIST;",
            "()L$CLASSNAME_LIST<L$CLASSNAME_TYPE_ADAPTER_FACTORY;>;"
        )
    }

    private fun generateInnerClasses(): List<Pair<String, ByteArray>> {
        return typeAdapterNames.entries.map { (type, typeAdapter) ->
            val wrappedInnerClassName = "${GENERATED_CLASS}_${typeAdapter.getSimpleName()}"
            val innerClassGenerator =
                TypeAdapterFactoryInnerClassGenerator(GENERATED_CLASS, type, typeAdapter)
            "$wrappedInnerClassName.class" to innerClassGenerator.generate()
        }
    }

    private fun String.getSimpleName(): String {
        return split(".").last()
    }
}