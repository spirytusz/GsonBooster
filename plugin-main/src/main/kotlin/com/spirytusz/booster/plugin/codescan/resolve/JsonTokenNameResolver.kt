package com.spirytusz.booster.plugin.codescan.resolve

import com.spirytusz.booster.processor.base.data.type.JsonTokenName

interface JsonTokenNameResolver {

    val resolvedJsonTokenName: JsonTokenName
}