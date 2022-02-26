package com.spirytusz.booster.plugin.codescan.impl.java.resolve

import com.intellij.psi.*
import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionCombinedResolver

class JavaStringExpressionCombinedResolver(
    private val value: PsiAnnotationMemberValue
) : StringExpressionCombinedResolver {

    private class JavaUnSupportOperatorException(msg: String) : IllegalArgumentException(msg)

    private class JavaTooComplexExpressionException(msg: String) : IllegalArgumentException(msg)

    override fun resolveCombinedExpression(): String {
        return resolvePsiAnnotationMemberValueLiteralExpression(value)
    }

    private fun resolvePsiAnnotationMemberValueLiteralExpression(value: PsiAnnotationMemberValue): String {
        return when (value) {
            is PsiReferenceExpression -> {
                val field = value.resolve() as? PsiField
                val initializer = field?.initializer ?: return ""
                resolvePsiAnnotationMemberValueLiteralExpression(initializer)
            }
            is PsiLiteralExpression -> {
                value.text.replace("\"", "")
            }
            is PsiArrayInitializerMemberValue -> {
                value.initializers.map {
                    resolvePsiAnnotationMemberValueLiteralExpression(it)
                }.joinToString(separator = "") { it }
            }
            is PsiBinaryExpression -> {
                if (value.operationTokenType.toString().toUpperCase() != "PLUS") {
                    val msg = "UnSupport Operator: ${value.text}"
                    throw JavaUnSupportOperatorException(msg)
                }
                val leftExpression = value.lOperand
                val rightExpression = value.rOperand
                if (leftExpression is PsiBinaryExpression || rightExpression is PsiBinaryExpression) {
                    throw IllegalStateException()
                }
                val leftImports = resolvePsiAnnotationMemberValueLiteralExpression(leftExpression)
                val rightImports = rightExpression?.let { resolvePsiAnnotationMemberValueLiteralExpression(it) } ?: ""
                leftImports + rightImports
            }
            is PsiPolyadicExpression -> {
                value.operands.map {
                    resolvePsiAnnotationMemberValueLiteralExpression(it)
                }.joinToString(separator = "") { it }
            }
            is PsiParenthesizedExpression -> {
                value.expression?.let { resolvePsiAnnotationMemberValueLiteralExpression(it) } ?: ""
            }
            else -> {
                val msg = "Too Complex Expression: ${value.text}"
                throw JavaTooComplexExpressionException(value.text)
            }
        }
    }
}