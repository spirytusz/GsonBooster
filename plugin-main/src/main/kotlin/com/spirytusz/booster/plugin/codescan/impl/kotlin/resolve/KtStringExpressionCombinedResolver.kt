package com.spirytusz.booster.plugin.codescan.impl.kotlin.resolve

import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionCombinedResolver
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.load.kotlin.toSourceElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.source.getPsi

class KtStringExpressionCombinedResolver(
    private val ktExpression: KtExpression
) : StringExpressionCombinedResolver {

    private class KtExpressionParseException(msg: String) : IllegalArgumentException(msg)

    private class KtTooComplexExpressionException(msg: String) : IllegalArgumentException(msg)

    private class KtInitializerParseException(msg: String) : IllegalArgumentException(msg)

    private class KtUnSupportOperatorException(msg: String) : IllegalArgumentException(msg)

    override fun resolveCombinedExpression(): String {
        val result = when (ktExpression) {
            is KtReferenceExpression -> resolveKtReferenceExpressionCombined(ktExpression)
            is KtConstantExpression -> resolveKtConstantExpressionCombined(ktExpression)
            is KtStringTemplateExpression -> resolveKtStringTemplateExpressionCombined(ktExpression)
            is KtDotQualifiedExpression -> resolveKtDotQualifiedExpressionCombined(ktExpression)
            is KtBinaryExpression -> resolveKtBinaryExpressionCombined(ktExpression)
            is KtParenthesizedExpression -> resolveKtParenthesizedExpressionCombined(ktExpression)
            else -> {
//                val msg = "Too Complex Expression: ${ktExpression.text}"
//                context.showErrorDialog("Too Complex Expression", msg)
                throw KtTooComplexExpressionException(ktExpression.text)
            }
        }
        return result
    }

    private fun resolveKtReferenceExpressionCombined(ktReferenceExpression: KtReferenceExpression): String {
        val bindingContext = ktExpression.analyze()
        val declarationDescriptor = bindingContext.get(BindingContext.REFERENCE_TARGET, ktReferenceExpression)
        val declaration = declarationDescriptor?.toSourceElement?.getPsi() as? KtProperty
        val initializer = declaration?.initializer
            ?: throw KtInitializerParseException(ktExpression.text)
        return KtStringExpressionCombinedResolver(initializer).resolveCombinedExpression()
    }

    private fun resolveKtConstantExpressionCombined(ktConstantExpression: KtConstantExpression): String {
        return ktConstantExpression.text
    }

    private fun resolveKtStringTemplateExpressionCombined(ktStringTemplateExpression: KtStringTemplateExpression): String {
        return ktStringTemplateExpression.entries.map { entry ->
            if (entry is KtLiteralStringTemplateEntry) {
                return@map entry.text
            }
            val entryExpression = entry.expression ?: throw KtExpressionParseException(entry.text)
            KtStringExpressionCombinedResolver(entryExpression).resolveCombinedExpression()
        }.joinToString(separator = "") {
            it
        }
    }

    private fun resolveKtDotQualifiedExpressionCombined(ktDotQualifiedExpression: KtDotQualifiedExpression): String {
        val receiver = ktDotQualifiedExpression.receiverExpression // left
        val selector = ktDotQualifiedExpression.selectorExpression // right
        return if (selector is KtNameReferenceExpression) {
            // org.example.const.Constants.KEY
            // Constants.KEY
            KtStringExpressionCombinedResolver(selector).resolveCombinedExpression()
        } else {
            when (receiver) {
                is KtNameReferenceExpression -> {
                    // KEY.toString()
                    KtStringExpressionCombinedResolver(receiver).resolveCombinedExpression()
                }
                is KtDotQualifiedExpression -> {
                    // Constants.KEY.toString()
                    // org.example.const.Constants.KEY.toString()
                    KtStringExpressionCombinedResolver(receiver).resolveCombinedExpression()
                }
                else -> {
                    // 123.toString()
                    KtStringExpressionCombinedResolver(receiver).resolveCombinedExpression()
                }
            }
        }
    }

    private fun resolveKtBinaryExpressionCombined(ktBinaryExpression: KtBinaryExpression): String {
        val operation = ktBinaryExpression.operationToken.toString().toUpperCase()
        if (operation != "PLUS") {
            throw KtUnSupportOperatorException(ktBinaryExpression.text)
        }
        // FIXME: 12.10.21 too complete expression...
        // like 3 + 2 + 1
        // left is KtBinaryExpression
        // right is KtConstantExpression
        if (ktBinaryExpression.left is KtBinaryExpression || ktBinaryExpression.right is KtBinaryExpression) {
            throw IllegalArgumentException("Too complex binary expression: ${ktBinaryExpression.text}")
        }
        val leftExpressionCombined = ktBinaryExpression.left?.let {
            KtStringExpressionCombinedResolver(it).resolveCombinedExpression()
        } ?: ""
        val rightExpressionCombined = ktBinaryExpression.right?.let {
            KtStringExpressionCombinedResolver(it).resolveCombinedExpression()
        } ?: ""
        return if (ktBinaryExpression.left is KtConstantExpression && ktBinaryExpression.right is KtConstantExpression) {
            (leftExpressionCombined.toLong() + rightExpressionCombined.toLong()).toString()
        } else {
            leftExpressionCombined + rightExpressionCombined
        }
    }

    private fun resolveKtParenthesizedExpressionCombined(ktParenthesizedExpression: KtParenthesizedExpression): String {
        val innerExpression = ktParenthesizedExpression.expression
            ?: throw IllegalArgumentException("Can not parse KtParenthesizedExpression: ${ktExpression.text}, innerExpression = null")
        return KtStringExpressionCombinedResolver(innerExpression).resolveCombinedExpression()
    }
}