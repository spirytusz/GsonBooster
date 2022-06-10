package com.spirytusz.booster.processor.scan.kapt

import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.spirytusz.booster.processor.base.extensions.kotlinType
import com.spirytusz.booster.processor.scan.kapt.base.AbstractKmTypeResolver
import com.spirytusz.booster.processor.scan.kapt.data.KaptKtType
import com.squareup.kotlinpoet.ClassName
import kotlinx.metadata.Flag
import kotlinx.metadata.KmClassifier
import kotlinx.metadata.KmType
import kotlinx.metadata.KmVariance
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.VariableElement

class KmTypeResolver(
    processingEnvironment: ProcessingEnvironment,
    private val belongingVariant: VariableElement?,
    kmType: KmType
) : AbstractKmTypeResolver(processingEnvironment, kmType) {

    override val resolvedKtType: KtType = resolveKtType()

    private fun resolveKtType(): KtType {
        return kmType.parseKmType()
    }

    private fun KmType.parseKmType(): KtType {
        val kmClassifier = classifier as KmClassifier.Class
        val name = kmClassifier.name.replace("/", ".")
        val nullable = Flag.Type.IS_NULLABLE(flags)
        val jsonTokenName = parseJsonTokenName()
        val generics = arguments.map {
            val variance = when (it.variance) {
                KmVariance.IN -> KtVariance.IN
                KmVariance.OUT -> KtVariance.OUT
                else -> KtVariance.INVARIANT
            }
            it.type!!.parseKmType().copy(variance = variance)
        }
        return KaptKtType(
            rawType = name,
            nullable = nullable,
            variance = KtVariance.INVARIANT,
            jsonTokenName = jsonTokenName,
            generics = generics,
            target = belongingVariant
        )
    }

    private fun KmType.parseJsonTokenName(): JsonTokenName {
        val kmClassifier = classifier as KmClassifier.Class
        val name = kmClassifier.name.replace("/", ".")
        return when {
            isPrimitive(this) -> {
                val kotlinType = ClassName.bestGuess(name).kotlinType() as ClassName
                when (kotlinType.simpleName) {
                    "Int" -> JsonTokenName.INT
                    "Long" -> JsonTokenName.LONG
                    "Float" -> JsonTokenName.FLOAT
                    "Double" -> JsonTokenName.DOUBLE
                    "String" -> JsonTokenName.STRING
                    "Boolean" -> JsonTokenName.BOOLEAN
                    else -> throw IllegalStateException("")
                }
            }
            isKotlinList(this) -> JsonTokenName.LIST
            isJavaList(this) -> JsonTokenName.JAVA_LIST
            isKotlinSet(this) -> JsonTokenName.SET
            isJavaSet(this) -> JsonTokenName.JAVA_SET
            isKotlinMap(this) -> JsonTokenName.MAP
            isJavaMap(this) -> JsonTokenName.JAVA_MAP
            isEnum(this) -> JsonTokenName.ENUM
            else -> JsonTokenName.OBJECT
        }
    }
}