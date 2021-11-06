package com.spirytusz.booster.processor.gen.api.funcgen.write

import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.stream.JsonWriter
import com.spirytusz.booster.processor.config.BoosterGenConfig
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.gen.api.funcgen.AbstractFunctionGenerator
import com.spirytusz.booster.processor.gen.api.funcgen.write.types.TypeWriteCodeGeneratorFactory
import com.spirytusz.booster.processor.gen.const.Constants.OBJECT
import com.spirytusz.booster.processor.gen.const.Constants.WRITER
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier

import com.squareup.kotlinpoet.ksp.toClassName

class BoosterWriteFuncGenerator(
    ksFile: KSFile,
    private val config: BoosterGenConfig
) : AbstractFunctionGenerator(ksFile) {

    fun generateWriteFunc(classScanner: AbstractClassScanner): FunSpec {
        return FunSpec.builder("write")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(WRITER, JsonWriter::class)
            .addParameter(OBJECT, classScanner.ksClass.toClassName().copy(nullable = true))
            .appendPreCheckNullCode()
            .beginObject(WRITER)
            .appendWriteCodes(classScanner)
            .endObject(WRITER).build()
    }

    private fun FunSpec.Builder.appendPreCheckNullCode(): FunSpec.Builder {
        return this.apply {
            beginControlFlow("if ($OBJECT == null)")
            addStatement("$WRITER.nullValue()")
            addStatement("return")
            endControlFlow()
        }
    }

    private fun FunSpec.Builder.appendWriteCodes(classScanner: AbstractClassScanner): FunSpec.Builder {
        return this.apply {
            classScanner.allProperties.filterNot { it.transient }.forEach { propertyDescriptor ->
                val typeWriteFuncGenerator =
                    TypeWriteCodeGeneratorFactory.create(propertyDescriptor)
                addWriteKeyCode(propertyDescriptor)
                typeWriteFuncGenerator.generate(this, propertyDescriptor)
            }
        }
    }

    private fun FunSpec.Builder.addWriteKeyCode(propertyDescriptor: PropertyDescriptor): FunSpec.Builder {
        return this.apply {
            val key = propertyDescriptor.keys
                .ifEmpty { listOf(propertyDescriptor.fieldName) }.first()
            addStatement("$WRITER.name(\"$key\")")
        }
    }
}