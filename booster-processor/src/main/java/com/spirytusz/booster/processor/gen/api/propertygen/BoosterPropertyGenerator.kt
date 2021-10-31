package com.spirytusz.booster.processor.gen.api.propertygen

import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.TypeAdapter
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.spirytusz.booster.processor.gen.const.Constants.GSON
import com.spirytusz.booster.processor.gen.extension.asTypeName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterFieldName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterName
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

class BoosterPropertyGenerator(
    private val ksFile: KSFile,
    private val classScanner: AbstractClassScanner,
    private val allClassScanners: Set<AbstractClassScanner>
) {

    @KotlinPoetKspPreview
    fun generateProperties(): Set<PropertySpec> {
        return classScanner.allProperties.asSequence().filterNot {
            it.transient
        }.map {
            getTypeDescriptorUntilNotCollection(it.type)
        }.filterNot {
            it.isPrimitive()
        }.distinctBy {
            it.getTypeAdapterFieldName()
        }.map { typeDescriptor ->
            createPropertySpecFromTypeDescriptor(typeDescriptor)
        }.toSet()
    }

    private fun getTypeDescriptorUntilNotCollection(typeDescriptor: TypeDescriptor): TypeDescriptor {
        var current = typeDescriptor
        while (current.isArray()) {
            current = current.typeArguments.first()
        }
        return current
    }

    @KotlinPoetKspPreview
    private fun createPropertySpecFromTypeDescriptor(typeDescriptor: TypeDescriptor): PropertySpec {
        val typeAdapterClassSimpleName = classScanner.getTypeAdapterName()
        val adapterFieldName = typeDescriptor.getTypeAdapterFieldName()
        val isAnnotatedByBooster = allClassScanners.any {
            it.ksClass.qualifiedName?.asString() == typeDescriptor.raw
        }
        val initializerCodeBlock = if (isAnnotatedByBooster) {
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement("%L($GSON)", typeAdapterClassSimpleName)
                .endControlFlow()
                .build()
        } else {
            CodeBlock.Builder()
                .beginControlFlow("lazy")
                .addStatement(
                    "$GSON.getAdapter(object: TypeToken<%L> {})",
                    typeDescriptor.flattenCanonical()
                )
                .endControlFlow()
                .build()
        }
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(classScanner.ksClass.asTypeName())
        }
        return PropertySpec.builder(adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(initializerCodeBlock)
            .build()
    }
}