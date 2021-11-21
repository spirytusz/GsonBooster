package com.spirytusz.booster.processor.scan.api

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.spirytusz.booster.processor.data.PropertyDescriptor
import com.spirytusz.booster.processor.data.TypeDescriptor
import com.spirytusz.booster.processor.extension.info
import com.spirytusz.booster.processor.scan.resolver.JsonTokenNameResolver

abstract class AbstractClassScanner(
    private val environment: SymbolProcessorEnvironment,
    private val resolver: Resolver,
    val ksClass: KSClassDeclaration
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

    val allProperties: Set<PropertyDescriptor> by lazy {
        (primaryConstructorProperties + classProperties).distinctBy { it.fieldName }.toSet()
    }

    private val tag = this::class.simpleName.toString()

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

    private val jsonTokenNameResolver = JsonTokenNameResolver(environment, resolver)

    protected fun createTypeDescriptorFromKSType(ksType: KSType): TypeDescriptor {
        val typeArguments = ksType.arguments.map {
            it to it.type?.resolve()
        }.mapNotNull { (typeArgument, ksType) ->
            ksType ?: return@mapNotNull null
            createTypeDescriptorFromKSType(ksType).copy(variance = typeArgument.variance)
        }.toList()
        return TypeDescriptor(
            raw = ksType.declaration.qualifiedName?.asString().toString(),
            nullability = ksType.nullability,
            variance = Variance.INVARIANT,
            jsonTokenName = jsonTokenNameResolver.resolve(ksType),
            typeArguments = typeArguments,
            origin = ksType
        )
    }

    private fun scanPrimaryConstructorProperties(): Set<PropertyDescriptor> {
        return ksClass.primaryConstructor?.parameters?.map {
            val primaryConstructProperty = createPropertyDescriptorFromKSValueParameter(it)
            environment.logger.info(
                tag,
                "${ksClass.qualifiedName?.asString()} primaryConstructProperty >>> $primaryConstructProperty"
            )
            primaryConstructProperty
        }?.toSet() ?: emptySet()
    }

    private fun scanClassProperties(): Set<PropertyDescriptor> {
        return ksClass.getAllProperties().asSequence().filterNot { ksProperty ->
            primaryConstructorProperties.any { it.fieldName == ksProperty.simpleName.asString() }
        }.map {
            val classProperty = createPropertyDescriptorFromKSPropertyDeclaration(it)
            environment.logger.info(
                tag,
                "${ksClass.qualifiedName?.asString()} classProperty >>> $classProperty"
            )
            classProperty
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