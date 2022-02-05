package com.spirytusz.booster.processor.scan.ksp.data

import com.google.devtools.ksp.symbol.KSNode
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance

data class KspKtType(
    override val rawType: String,
    override val nullable: Boolean,
    override val variance: KtVariance,
    override val jsonTokenName: JsonTokenName,
    override val generics: List<KtType>,
    override val target: KSNode?
) : KtType, IKsNodeOwner {
    override fun copy(nullable: Boolean, variance: KtVariance): KtType {
        return KspKtType(
            rawType = this.rawType,
            nullable = nullable,
            variance = variance,
            jsonTokenName = this.jsonTokenName,
            generics = this.generics,
            target = this.target
        )
    }
}
