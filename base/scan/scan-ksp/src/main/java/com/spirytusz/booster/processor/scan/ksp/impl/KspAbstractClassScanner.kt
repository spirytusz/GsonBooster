package com.spirytusz.booster.processor.scan.ksp.impl

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.scan.ksp.data.KspKtField
import com.spirytusz.booster.processor.scan.ksp.data.KspKtType

abstract class KspAbstractClassScanner(
    private val environment: SymbolProcessorEnvironment,
    private val resolver: Resolver,
    private val ksClass: KSClassDeclaration,
    private val logger: MessageLogger
) : ClassScanner {

    companion object {
        private const val SERIALIZED_NAME_SIMPLE_NAME = "SerializedName"
        private const val SERIALIZED_NAME_VALUE = "value"
        private const val SERIALIZED_NAME_ALTERNATE = "alternate"
    }

    final override val ktFields: List<KtField> by lazy {
        scanPrimaryConstructorKtFields() + scanClassKtFields()
    }

    private val jsonTokenNameResolver = JsonTokenNameResolver(resolver, logger)

    /**
     * constructor properties
     */
    abstract fun createKtFieldFromKSValueParameter(
        ksValueParameter: KSValueParameter
    ): KspKtField

    /**
     * class body properties
     */
    abstract fun createKtFieldFromKSPropertyDeclaration(
        ksPropertyDeclaration: KSPropertyDeclaration
    ): KspKtField

    protected fun createKtTypeFromKSType(ksType: KSType): KspKtType {
        val generics = ksType.arguments.map {
            it to it.type?.resolve()
        }.mapNotNull { (typeArgument, ksType) ->
            ksType ?: return@mapNotNull null
            createKtTypeFromKSType(ksType).copy(variance = typeArgument.variance.toKtVariant())
        }.toList()
        return KspKtType(
            rawType = ksType.declaration.qualifiedName?.asString().toString(),
            nullable = ksType.nullability == Nullability.NULLABLE,
            variance = Variance.INVARIANT.toKtVariant(),
            jsonTokenName = jsonTokenNameResolver.resolve(ksType),
            generics = generics,
            target = null
        )
    }

    private fun scanPrimaryConstructorKtFields(): List<KspKtField> {
        return ksClass.primaryConstructor?.parameters?.map {
            val primaryConstructProperty = createKtFieldFromKSValueParameter(it)
            logger.info(
                "${ksClass.qualifiedName?.asString()} primaryConstructProperty >>> $primaryConstructProperty"
            )
            primaryConstructProperty
        }?.toList() ?: emptyList()
    }

    private fun scanClassKtFields(): List<KspKtField> {
        return ksClass.getDeclaredProperties().map {
            val classProperty = createKtFieldFromKSPropertyDeclaration(it)
            logger.info(
                "${ksClass.qualifiedName?.asString()} classProperty >>> $classProperty"
            )
            classProperty
        }.toList()
    }

    private fun scanSuperKtFields(): List<KspKtField> {
        return ksClass.getAllSuperTypes().map {
            it.declaration as KSClassDeclaration
        }.map {
            KspClassScannerFactory.create(
                environment,
                resolver,
                it,
                logger
            ).ktFields
        }.flatten().map {
            it as KspKtField
        }.toList()
    }

    @Suppress("UNCHECKED_CAST")
    protected fun resolveKeys(ksAnnotation: List<KSAnnotation>): List<String> {
        val serializedName = ksAnnotation.find {
            it.shortName.asString() == SERIALIZED_NAME_SIMPLE_NAME
        } ?: return emptyList()

        val valueParam = serializedName.arguments.find {
            it.name?.asString() == SERIALIZED_NAME_VALUE
        }?.value as String
        val alternateParam = serializedName.arguments.find {
            it.name?.asString() == SERIALIZED_NAME_ALTERNATE
        }?.value as? List<String> ?: listOf()
        return listOf(valueParam) + alternateParam.toList()
    }

    private fun Variance.toKtVariant(): KtVariance {
        return when (this) {
            Variance.INVARIANT, Variance.STAR -> KtVariance.INVARIANT
            Variance.COVARIANT -> KtVariance.IN
            Variance.CONTRAVARIANT -> KtVariance.OUT
        }
    }
}