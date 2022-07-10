package com.spirytusz.aggregation.plugin.asm.extensions

import com.spirytusz.booster.contract.Constants.ClassNames.CLASSNAME_OBJECT
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

fun ClassWriter.writeNoArgsConstructor(lineNumber: Int, accFlag: Int = Opcodes.ACC_PUBLIC) {
    val methodVisitor = visitMethod(accFlag, "<init>", "()V", null, null)
    methodVisitor.visitCode()
    val label = Label()
    methodVisitor.visitLabel(label)
    methodVisitor.visitLineNumber(lineNumber, label)
    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
    methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, CLASSNAME_OBJECT, "<init>", "()V", false)
    methodVisitor.visitInsn(Opcodes.RETURN)
    methodVisitor.visitMaxs(-1, -1)
    methodVisitor.visitEnd()
}