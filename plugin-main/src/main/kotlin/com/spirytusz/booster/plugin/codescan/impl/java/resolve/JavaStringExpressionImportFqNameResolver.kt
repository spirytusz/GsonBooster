package com.spirytusz.booster.plugin.codescan.impl.java.resolve

import com.intellij.psi.*
import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionImportFqNamesResolver
import org.jetbrains.kotlin.idea.search.getKotlinFqName
import org.jetbrains.kotlin.name.FqName

class JavaStringExpressionImportFqNameResolver(
    private val value: PsiAnnotationMemberValue
) : StringExpressionImportFqNamesResolver {

    companion object {
        private const val TAG = "StringExpressionImportFqNamesResolver"
    }

    override fun resolveImportNames(): Set<FqName> {
        return resolvePsiAnnotationMemberValueImportNames(value)
    }

    private fun resolvePsiAnnotationMemberValueImportNames(value: PsiAnnotationMemberValue): Set<FqName> {
        return when (value) {
            is PsiReferenceExpression -> {
                val field = value.resolve() as? PsiField
                val fqName = field?.getKotlinFqName()?.toString() ?: return emptySet()
                val resultFqName = fqName.replace(".Companion", "")
                setOf(FqName(resultFqName))
            }
            is PsiLiteralExpression -> {
                setOf()
            }
            is PsiArrayInitializerMemberValue -> {
                value.initializers.map {
                    resolvePsiAnnotationMemberValueImportNames(it)
                }.flatten().toSet()
            }
            is PsiBinaryExpression -> {
                val leftImports = resolvePsiAnnotationMemberValueImportNames(value.lOperand)
                val rightImports = value.rOperand?.let { resolvePsiAnnotationMemberValueImportNames(it) } ?: setOf()
                leftImports + rightImports
            }
            is PsiPolyadicExpression -> {
                value.operands.map {
                    resolvePsiAnnotationMemberValueImportNames(it)
                }.flatten().toSet()
            }
            is PsiParenthesizedExpression -> {
                value.expression?.let { resolvePsiAnnotationMemberValueImportNames(it) } ?: setOf()
            }
            else -> {
                val msg = "Too Complex Expression: ${value.text}"
                throw IllegalStateException(msg)
            }
        }
    }
}