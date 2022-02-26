package com.spirytusz.booster.plugin.codescan.resolve

import org.jetbrains.kotlin.name.FqName

interface StringExpressionImportFqNamesResolver {

    fun resolveImportNames(): Set<FqName>
}