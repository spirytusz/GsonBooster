package com.spirytusz.booster.plugin.codescan.resolve

import org.jetbrains.kotlin.name.FqName

interface StringExpressionResolver {

    val literalExpression: String

    val importFqNames: Set<FqName>

    val combinedExpression: String
}