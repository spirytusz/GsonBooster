package com.spirytusz.booster.plugin.codescan.impl.kotlin.resolve

import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionResolver
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression

/**
 * 把一个String表达式解析成需要import的全限定名[importFqNames]
 */
class KtStringExpressionResolver(
    private val ktExpression: KtExpression
) : StringExpressionResolver {

    companion object {
        private const val TAG = "KtStrExpResolver"
    }

    override val literalExpression: String by lazy {
        ktExpression.text
    }

    override val combinedExpression by lazy {
        resolveCombinedExpression()
    }

    override val importFqNames by lazy {
        resolveImportFqNames()
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

        other as KtStringExpressionResolver

        if (literalExpression != other.literalExpression) return false
        if (combinedExpression != other.combinedExpression) return false
        if (importFqNames != other.importFqNames) return false

        return true
    }

    override fun hashCode(): Int {
        var result = literalExpression.hashCode()
        result = 31 * result + combinedExpression.hashCode()
        result = 31 * result + importFqNames.hashCode()
        return result
    }

    private fun resolveCombinedExpression(): String {
        val result = KtStringExpressionCombinedResolver(ktExpression).resolveCombinedExpression()
        //LogUtils.debug(TAG, "resolveCombinedExpression() >>> $result")
        return result
    }

    private fun resolveImportFqNames(): Set<FqName> {
        val result = KtStringExpressionImportFqNameResolver(ktExpression).resolveImportNames()
        //LogUtils.debug(TAG, "resolveImportFqNames() >>> $result")
        return result
    }
}