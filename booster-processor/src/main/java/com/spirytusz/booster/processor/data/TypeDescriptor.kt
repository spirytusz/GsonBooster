package com.spirytusz.booster.processor.data

import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance

/**
 * 代表一个类型
 *
 * @param raw 全限定名
 * @param nullability 可空性
 * @param variance 协变、逆变、不变、通配符
 * @param typeArguments 泛型
 */
data class TypeDescriptor(
    val raw: String,
    val nullability: Nullability,
    val variance: Variance,
    val jsonTokenName: JsonTokenName,
    val typeArguments: Set<TypeDescriptor>
) {

    fun nullable(): Boolean = nullability == Nullability.NULLABLE

    fun copy(
        nullability: Nullability = this.nullability,
        variance: Variance = this.variance
    ): TypeDescriptor {
        return TypeDescriptor(
            raw = this.raw,
            nullability = nullability,
            variance = variance,
            jsonTokenName = jsonTokenName,
            typeArguments = typeArguments
        )
    }

    fun flattenCanonical(): String = flattenTransform()

    fun flattenSimple(): String = flattenTransform {
        it.split(".").last()
    }

    private fun flattenTransform(transform: ((String) -> String)? = null): String = buildString {
        val varianceText = when (variance) {
            Variance.INVARIANT -> ""
            Variance.CONTRAVARIANT -> "in "
            Variance.COVARIANT -> "out "
            Variance.STAR -> "*"
        }
        append(varianceText)
        append(transform?.invoke(raw) ?: raw)
        val typeArgumentPlainTexts = typeArguments.map { it.flattenCanonical() }
        if (typeArgumentPlainTexts.isNotEmpty()) {
            append("<${typeArgumentPlainTexts.joinToString { it }}>")
        }
        if (nullable()) {
            append("?")
        }
    }
}