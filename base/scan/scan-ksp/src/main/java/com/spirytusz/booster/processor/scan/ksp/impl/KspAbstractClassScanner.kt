package com.spirytusz.booster.processor.scan.ksp.impl

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariableType
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.scan.ksp.KspClassScannerFactory
import com.spirytusz.booster.processor.scan.ksp.data.IKsNodeOwner
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
        primaryConstructorKtFields + classBodyKtFields + scanSuperKtFields()
    }

    final override val classKtType: KtType by lazy {
        resolveClassType()
    }

    private val primaryConstructorKtFields by lazy { scanPrimaryConstructorKtFields() }

    private val classBodyKtFields by lazy { scanClassKtFields() }

    private val jsonTokenNameResolver by lazy { JsonTokenNameResolver(resolver, logger) }

    /**
     * constructor properties
     */
    internal abstract fun createKtFieldFromKSValueParameter(
        ksValueParameter: KSValueParameter
    ): KspKtField

    /**
     * class body properties
     */
    internal abstract fun createKtFieldFromKSPropertyDeclaration(
        ksPropertyDeclaration: KSPropertyDeclaration
    ): KspKtField

    private fun resolveClassType(): KtType {
        fun resolveGenerics(ksTypeParameter: KSTypeParameter): KtType {
            return object : KtVariableType(
                rawType = ksTypeParameter.simpleName.asString(),
                nullable = false,
                variance = KtVariance.INVARIANT,
                jsonTokenName = JsonTokenName.OBJECT,
                generics = ksTypeParameter.typeParameters.map { resolveGenerics(it) }
            ), IKsNodeOwner {
                override val target: KSNode = ksTypeParameter
            }
        }
        return KspKtType(
            rawType = ksClass.qualifiedName?.asString().toString(),
            nullable = false,
            variance = KtVariance.INVARIANT,
            jsonTokenName = JsonTokenName.OBJECT,
            generics = ksClass.typeParameters.map { resolveGenerics(it) },
            target = ksClass
        )
    }

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
        return ksClass.getDeclaredProperties().filter {
            ksClass.primaryConstructor?.parameters?.none { ksValueParameter ->
                ksValueParameter.name?.asString() == it.simpleName.asString()
            } == true
        }.map {
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
            it.copy(declarationScope = DeclarationScope.SUPERS) as KspKtField
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