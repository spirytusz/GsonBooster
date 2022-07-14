package com.spirytusz.aggregation.plugin.asm

import com.spirytusz.aggregation.plugin.asm.extensions.writeNoArgsConstructor
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_CLASS
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_GSON
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_OBJECT
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_TYPE_ADAPTER
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_TYPE_ADAPTER_FACTORY
import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_TYPE_TOKEN
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


class TypeAdapterFactoryInnerClassGenerator(
    private val outerClassName: String,
    type: String,
    typeAdapter: String
) {

    private val typeName = type.replace(".", "/")
    private val typeAdapterClassName = typeAdapter.replace(".", "/")

    fun generate(): ByteArray {
        val classWriter = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER,
            "${outerClassName}_${typeAdapterClassName.split("/").last()}",
            null,
            CLASSNAME_OBJECT,
            arrayOf(CLASSNAME_TYPE_ADAPTER_FACTORY)
        )

        classWriter.writeNoArgsConstructor(lineNumber = 8)
        classWriter.visitCreateMethod()
        classWriter.visitEnd()

        return classWriter.toByteArray()
    }

    private fun ClassWriter.visitCreateMethod() {
        val methodVisitor = visitMethod(
            Opcodes.ACC_PUBLIC,
            "create",
            "(L$CLASSNAME_GSON;L$CLASSNAME_TYPE_TOKEN;)L$CLASSNAME_TYPE_ADAPTER;",
            "<T:L$CLASSNAME_OBJECT;>(L$CLASSNAME_GSON;L$CLASSNAME_TYPE_TOKEN<TT;>;)L$CLASSNAME_TYPE_ADAPTER<TT;>;",
            null
        )
        methodVisitor.visitCode()

        val label = Label()
        methodVisitor.visitLabel(label)
        methodVisitor.visitLineNumber(11, label)
        methodVisitor.visitLdcInsn(Type.getObjectType(typeName))
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            CLASSNAME_TYPE_TOKEN,
            "getRawType",
            "()L$CLASSNAME_CLASS;",
            false
        )
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            CLASSNAME_CLASS,
            "isAssignableFrom",
            "(L$CLASSNAME_CLASS;)Z",
            false
        )
        val label1 = Label()
        methodVisitor.visitJumpInsn(Opcodes.IFEQ, label1)
        methodVisitor.visitTypeInsn(Opcodes.NEW, typeAdapterClassName)
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            typeAdapterClassName,
            "<init>",
            "(L$CLASSNAME_GSON;)V",
            false
        )

        val label2 = Label()
        methodVisitor.visitJumpInsn(Opcodes.GOTO, label2)
        methodVisitor.visitLabel(label1)
        methodVisitor.visitFrame(-1, -1, null, -1, null)
        methodVisitor.visitInsn(Opcodes.ACONST_NULL)

        methodVisitor.visitLabel(label2)
        methodVisitor.visitFrame(-1, -1, null, -1, null)
        methodVisitor.visitInsn(Opcodes.ARETURN)

        methodVisitor.visitMaxs(-1, -1)
        methodVisitor.visitEnd()
    }
}