package com.spirytusz.booster.processor.scan

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.data.TypeDescriptor

abstract class AbstractClassScanner(
    private val environment: SymbolProcessorEnvironment,
    private val ksClass: KSClassDeclaration
) {

    companion object {
        private const val SERIALIZED_NAME_SIMPLE_NAME = "SerializedName"
        private const val SERIALIZED_NAME_VALUE = "value"
        private const val SERIALIZED_NAME_ALTERNATE = "alternate"
    }

    val containingFile: KSFile by lazy {
        ksClass.containingFile!!
    }

    val primaryConstructorProperties: Set<PropertyDescriptor> by lazy {
        scanPrimaryConstructorProperties()
    }

    val classProperties: Set<PropertyDescriptor> by lazy {
        scanClassProperties()
    }

    /**
     * constructor properties
     */
    abstract fun createPropertyDescriptorFromKSValueParameter(
        ksValueParameter: KSValueParameter
    ): PropertyDescriptor

    /**
     * class body properties
     */
    abstract fun createPropertyDescriptorFromKSPropertyDeclaration(
        ksPropertyDeclaration: KSPropertyDeclaration
    ): PropertyDescriptor

    protected fun createTypeDescriptorFromKSType(ksType: KSType): TypeDescriptor {
        val typeArguments = ksType.arguments.map {
            it to it.type?.resolve()
        }.mapNotNull { (typeArgument, ksType) ->
            ksType ?: return@mapNotNull null
            createTypeDescriptorFromKSType(ksType).copy(variance = typeArgument.variance)
        }.toSet()
        return TypeDescriptor(
            raw = ksType.declaration.qualifiedName?.asString().toString(),
            nullability = ksType.nullability,
            variance = Variance.INVARIANT,
            typeArguments = typeArguments
        )
    }

    private fun scanPrimaryConstructorProperties(): Set<PropertyDescriptor> {
        return ksClass.primaryConstructor?.parameters?.map {
            createPropertyDescriptorFromKSValueParameter(it)
        }?.toSet() ?: emptySet()
    }

    private fun scanClassProperties(): Set<PropertyDescriptor> {
        return ksClass.getAllProperties().asSequence().filterNot { ksProperty ->
            primaryConstructorProperties.any { it.fieldName == ksProperty.simpleName.asString() }
        }.map {
            createPropertyDescriptorFromKSPropertyDeclaration(it)
        }.toSet()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun resolveKeys(ksAnnotation: List<KSAnnotation>): Set<String> {
        val serializedName = ksAnnotation.find {
            it.shortName.asString() == SERIALIZED_NAME_SIMPLE_NAME
        } ?: return emptySet()

        val valueParam = serializedName.arguments.find {
            it.name?.asString() == SERIALIZED_NAME_VALUE
        }?.value as String
        val alternateParam = serializedName.arguments.find {
            it.name?.asString() == SERIALIZED_NAME_ALTERNATE
        }?.value as? List<String> ?: listOf()
        return setOf(valueParam) + alternateParam.toSet()
    }
}