package com.spirytusz.booster.processor.gen.api.propertygen

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.spirytusz.booster.processor.gen.const.Constants.GSON
import com.spirytusz.booster.processor.gen.extension.asTypeName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterFieldName
import com.spirytusz.booster.processor.gen.extension.getTypeAdapterName
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.*


class BoosterPropertyGenerator(
    private val environment: SymbolProcessorEnvironment,
    private val ksFile: KSFile,
    private val allClassScanners: Set<AbstractClassScanner>
) {


    fun generateProperties(classScanner: AbstractClassScanner): Set<PropertySpec> {
        return classScanner.allProperties.asSequence().filterNot {
            it.transient
        }.map {
            getTypeDescriptorUntilNotCollection(it.type)
        }.filterNot {
            it.isPrimitive()
        }.distinctBy {
            it.getTypeAdapterFieldName()
        }.map { typeDescriptor ->
            createPropertySpecFromTypeDescriptor(classScanner, typeDescriptor)
        }.toSet()
    }

    private fun getTypeDescriptorUntilNotCollection(typeDescriptor: TypeDescriptor): TypeDescriptor {
        var current = typeDescriptor
        while (current.isArray()) {
            current = current.typeArguments.first()
        }
        return current
    }


    private fun createPropertySpecFromTypeDescriptor(
        classScanner: AbstractClassScanner,
        typeDescriptor: TypeDescriptor
    ): PropertySpec {
        val typeAdapterClassSimpleName = typeDescriptor.getTypeAdapterName()
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
                    "$GSON.getAdapter(object: %T<%T>() {})",
                    TypeToken::class,
                    typeDescriptor.asTypeName()
                )
                .endControlFlow()
                .build()
        }
        val adapterType = with(ParameterizedTypeName.Companion) {
            TypeAdapter::class.asClassName().parameterizedBy(typeDescriptor.asTypeName())
        }
        return PropertySpec.builder(adapterFieldName, adapterType, KModifier.PRIVATE)
            .delegate(initializerCodeBlock)
            .build()
    }
}