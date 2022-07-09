package com.spirytusz.aggregation.plugin.asm

import org.objectweb.asm.*

class BundleClassGenerator(
    private val typeAdapterNames: List<String>,
    private val typeAdapterFactoryNames: List<String>
) {

    companion object {
        const val GENERATED_CLASS = "com/spirytusz/booster/aggregation/runtime/BoosterAggregationRegistry"

        private const val OBJECT = "java/lang/Object"
        private const val LIST = "java/util/List"
        private const val ARRAY_LIST = "java/util/ArrayList"
        private const val TYPE_ADAPTER = "com/google/gson/TypeAdapter"
        private const val TYPE_ADAPTER_FACTORY = "com/google/gson/TypeAdapterFactory"

        private const val ADAPTER_FIELD_NAME = "adapters"
        private const val FACTORY_FIELD_NAME = "factories"
    }

    private var currentLineNumber = 0

    fun generate(): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER,
            GENERATED_CLASS,
            null,
            OBJECT,
            arrayOf()
        )
        classWriter.visitField(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL or Opcodes.ACC_STATIC,
            ADAPTER_FIELD_NAME,
            "L$LIST;",
            "L$LIST<L$TYPE_ADAPTER;>;",
            null
        )
        classWriter.visitField(
            Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL or Opcodes.ACC_STATIC,
            FACTORY_FIELD_NAME,
            "L$LIST;",
            "L$LIST<L$TYPE_ADAPTER_FACTORY;>;",
            null
        )
        classWriter.visitConstructor()
        classWriter.visitStaticBlock()
        classWriter.generateGetters()
        return classWriter.toByteArray()
    }

    private fun ClassVisitor.visitConstructor() {
        val methodVisitor = visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
        methodVisitor.visitCode()
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLineNumber(9, label1)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT, "<init>", "()V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitEnd()
    }

    private fun ClassVisitor.visitStaticBlock() {
        currentLineNumber = 16

        fun MethodVisitor.initStaticField() {
            val label1 = Label()
            visitLabel(label1)
            visitLineNumber(11, label1)
            visitTypeInsn(Opcodes.NEW, ARRAY_LIST)
            visitInsn(Opcodes.DUP)
            visitMethodInsn(Opcodes.INVOKESPECIAL, ARRAY_LIST, "<init>", "()V", false)
            visitFieldInsn(Opcodes.PUTSTATIC, GENERATED_CLASS, ADAPTER_FIELD_NAME, "L$LIST;")

            val label2 = Label()
            visitLabel(label2)
            visitLineNumber(13, label2)
            visitTypeInsn(Opcodes.NEW, ARRAY_LIST)
            visitInsn(Opcodes.DUP)
            visitMethodInsn(Opcodes.INVOKESPECIAL, ARRAY_LIST, "<init>", "()V", false)
            visitFieldInsn(Opcodes.PUTSTATIC, GENERATED_CLASS, FACTORY_FIELD_NAME, "L$LIST;")
        }

        fun MethodVisitor.putTypeAdapters() {
            typeAdapterNames.forEach { typeAdapterName ->
                val formattedName = typeAdapterName.replace(".", "/")
                val label = Label()
                visitLabel(label)
                visitLineNumber(currentLineNumber++, label)
                visitFieldInsn(Opcodes.GETSTATIC, GENERATED_CLASS, ADAPTER_FIELD_NAME, "L$LIST;")
                visitTypeInsn(Opcodes.NEW, formattedName)
                visitInsn(Opcodes.DUP)
                visitMethodInsn(Opcodes.INVOKESPECIAL, formattedName, "<init>", "()V", false)
                visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true)
                visitInsn(Opcodes.POP)
            }
        }

        fun MethodVisitor.putTypeAdapterFactories() {
            currentLineNumber++

            typeAdapterFactoryNames.forEach { typeAdapterFactoryName ->
                val formattedName = typeAdapterFactoryName.replace(".", "/")
                val label = Label()
                visitLabel(label)
                visitLineNumber(currentLineNumber++, label)
                visitFieldInsn(Opcodes.GETSTATIC, GENERATED_CLASS, FACTORY_FIELD_NAME, "L$LIST;")
                visitTypeInsn(Opcodes.NEW, formattedName)
                visitInsn(Opcodes.DUP)
                visitMethodInsn(Opcodes.INVOKESPECIAL, formattedName, "<init>", "()V", false)
                visitMethodInsn(Opcodes.INVOKEINTERFACE, LIST, "add", "(Ljava/lang/Object;)Z", true)
                visitInsn(Opcodes.POP)
            }
        }

        val methodVisitor = visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null)
        methodVisitor.visitCode()
        methodVisitor.initStaticField()
        methodVisitor.putTypeAdapters()
        methodVisitor.putTypeAdapterFactories()

        val label = Label()
        methodVisitor.visitLabel(label)
        methodVisitor.visitLineNumber(currentLineNumber++, label)
        methodVisitor.visitInsn(Opcodes.RETURN)

        methodVisitor.visitEnd()
    }

    private fun ClassVisitor.generateGetters() {

        fun generateListGetter(fieldName: String, argumentType: String) {
            val methodVisitor = visitMethod(
                Opcodes.ACC_PROTECTED or Opcodes.ACC_STATIC,
                "get${fieldName.replaceFirstChar { it.uppercaseChar() }}",
                "()L$LIST;",
                "()L$LIST<L$argumentType;>;",
                null
            )
            methodVisitor.visitCode()
            val label = Label()
            methodVisitor.visitLabel(label)
            methodVisitor.visitLineNumber(currentLineNumber++, label)
            methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, GENERATED_CLASS, fieldName, "L$LIST;")
            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitEnd()
        }

        currentLineNumber += 3
        generateListGetter(ADAPTER_FIELD_NAME, TYPE_ADAPTER)

        currentLineNumber += 3
        generateListGetter(FACTORY_FIELD_NAME, TYPE_ADAPTER_FACTORY)
    }
}