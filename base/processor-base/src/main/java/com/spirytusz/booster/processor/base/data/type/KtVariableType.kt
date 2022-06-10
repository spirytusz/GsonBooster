package com.spirytusz.booster.processor.base.data.type

open class KtVariableType(
    override val rawType: String,
    override val nullable: Boolean,
    override val variance: KtVariance,
    override val jsonTokenName: JsonTokenName,
    override val generics: List<KtType>
) : KtType {

    override fun copy(nullable: Boolean, variance: KtVariance): KtVariableType {
        return KtVariableType(
            rawType = this.rawType,
            nullable = nullable,
            variance = variance,
            jsonTokenName = this.jsonTokenName,
            generics = this.generics
        )
    }
}