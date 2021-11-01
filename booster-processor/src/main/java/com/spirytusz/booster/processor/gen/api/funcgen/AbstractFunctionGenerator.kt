package com.spirytusz.booster.processor.gen.api.funcgen

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec

abstract class AbstractFunctionGenerator(protected val ksFile: KSFile) {

    protected fun FunSpec.Builder.beginObject(obj: String): FunSpec.Builder = this.apply {
        addStatement("$obj.beginObject()")
    }

    protected fun FunSpec.Builder.endObject(obj: String): FunSpec.Builder = this.apply {
        addStatement("$obj.endObject()")
    }

    protected fun CodeBlock.Builder.beginArray(obj: String): CodeBlock.Builder = this.apply {
        addStatement("$obj.beginArray()")
    }

    protected fun CodeBlock.Builder.endArray(obj: String): CodeBlock.Builder = this.apply {
        addStatement("$obj.endArray()")
    }
}