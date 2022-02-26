package com.spirytusz.booster.plugin.codescan.impl.kotlin.resolve

import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionImportFqNamesResolver
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

class KtStringExpressionImportFqNameResolver(
    private val ktExpression: KtExpression
) : StringExpressionImportFqNamesResolver {

    private class KtTooComplexExpressionException(msg: String) : IllegalArgumentException(msg)

    override fun resolveImportNames(): Set<FqName> {
        val result = when (ktExpression) {
            is KtReferenceExpression -> resolveReferenceExpressionImports(ktExpression)
            is KtConstantExpression -> setOf()
            is KtStringTemplateExpression -> resolveStringTemplateExpressionImports(ktExpression)
            is KtDotQualifiedExpression -> resolveDotQualifiedExpressionImports(ktExpression)
            is KtBinaryExpression -> resolveBinaryExpressionImports(ktExpression)
            is KtParenthesizedExpression -> {
                ktExpression.expression?.let { innerExpression ->
                    KtStringExpressionImportFqNameResolver(innerExpression).resolveImportNames()
                } ?: setOf()
            }
            else -> {
                throw KtTooComplexExpressionException(ktExpression.text)
            }
        }
        return result
    }

    private fun resolveReferenceExpressionImports(ktReferenceExpression: KtReferenceExpression): Set<FqName> {
        val bindingContext = ktExpression.analyze()
        val declarationDescriptor =
            bindingContext.get(BindingContext.REFERENCE_TARGET, ktReferenceExpression) ?: return setOf()
        return setOf(FqName(declarationDescriptor.fqNameSafe.toString()))
    }

    private fun resolveStringTemplateExpressionImports(ktStringTemplateExpression: KtStringTemplateExpression): Set<FqName> {
        return ktStringTemplateExpression.entries.asSequence().filter {
            it !is KtLiteralStringTemplateEntry
        }.mapNotNull { stringTemplateEntry ->
            stringTemplateEntry.expression
        }.map {
            KtStringExpressionImportFqNameResolver(it).resolveImportNames()
        }.flatten().toSet()
    }

    private fun resolveDotQualifiedExpressionImports(ktDotQualifiedExpression: KtDotQualifiedExpression): Set<FqName> {
        val receiver = ktDotQualifiedExpression.receiverExpression // left
        val selector = ktDotQualifiedExpression.selectorExpression // right
        return if (selector is KtNameReferenceExpression) {
            // org.example.const.Constants.KEY
            // Constants.KEY
            KtStringExpressionImportFqNameResolver(selector).resolveImportNames()
        } else {
            when (receiver) {
                is KtNameReferenceExpression -> {
                    // KEY.toString()
                    KtStringExpressionImportFqNameResolver(receiver).resolveImportNames()
                }
                is KtDotQualifiedExpression -> {
                    // Constants.KEY.toString()
                    // org.example.const.Constants.KEY.toString()
                    KtStringExpressionImportFqNameResolver(receiver).resolveImportNames()
                }
                else -> {
                    // 123.toString()
                    KtStringExpressionImportFqNameResolver(receiver).resolveImportNames()
                }
            }
        }
    }

    private fun resolveBinaryExpressionImports(ktBinaryExpression: KtBinaryExpression): Set<FqName> {
        val leftResolver = ktBinaryExpression.left?.let { KtStringExpressionImportFqNameResolver(it) }
        val rightResolver = ktBinaryExpression.right?.let { KtStringExpressionImportFqNameResolver(it) }
        val leftExpressionImports = leftResolver?.resolveImportNames() ?: setOf()
        val rightExpressionImports = rightResolver?.resolveImportNames() ?: setOf()
        return (leftExpressionImports + rightExpressionImports).toSet()
    }
}