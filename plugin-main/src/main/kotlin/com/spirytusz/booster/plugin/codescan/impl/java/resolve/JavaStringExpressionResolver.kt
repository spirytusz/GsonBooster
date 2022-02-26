package com.spirytusz.booster.plugin.codescan.impl.java.resolve

import com.intellij.psi.PsiAnnotationMemberValue
import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionResolver
import org.jetbrains.kotlin.name.FqName

class JavaStringExpressionResolver(
    private val value: PsiAnnotationMemberValue
) : StringExpressionResolver {

    companion object {
        private const val TAG = "JavaStrExpResolver"
    }

    override val literalExpression: String by lazy {
        value.text
    }

    override val importFqNames: Set<FqName> by lazy {
        resolveImportFqNames()
    }

    override val combinedExpression: String by lazy {
        resolveCombinedExpression()
    }

    override fun toString(): String {
        return buildString {
            append("combinedExpression=$combinedExpression\t")
            append("importFqNames=$importFqNames\t")
            append("literalExpression=$literalExpression\t")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JavaStringExpressionResolver

        if (literalExpression != other.literalExpression) return false
        if (combinedExpression != other.combinedExpression) return false
        if (importFqNames != other.importFqNames) return false

        return true
    }

    override fun hashCode(): Int {
        var result = literalExpression?.hashCode() ?: 0
        result = 31 * result + combinedExpression.hashCode()
        result = 31 * result + importFqNames.hashCode()
        return result
    }

    private fun resolveCombinedExpression(): String {
        val result = JavaStringExpressionCombinedResolver(value).resolveCombinedExpression()
//        LogUtils.debug(TAG, "resolveCombinedExpression() >>> $result")
        return result
    }

    private fun resolveImportFqNames(): Set<FqName> {
        val result = JavaStringExpressionImportFqNameResolver(value).resolveImportNames()
//        LogUtils.debug(TAG, "resolveImportFqNames() >>> $result")
        return result
    }
}