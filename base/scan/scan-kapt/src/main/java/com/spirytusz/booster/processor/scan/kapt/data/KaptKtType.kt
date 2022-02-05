package com.spirytusz.booster.processor.scan.kapt.data

import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance
import javax.lang.model.element.Element

data class KaptKtType(
    override val rawType: String,
    override val nullable: Boolean,
    override val variance: KtVariance,
    override val jsonTokenName: JsonTokenName,
    override val generics: List<KtType>,
    override val target: Element?
) : KtType, IElementOwner {
    override fun copy(nullable: Boolean, variance: KtVariance): KtType {
        return KaptKtType(
            rawType = this.rawType,
            nullable = nullable,
            variance = variance,
            jsonTokenName = this.jsonTokenName,
            generics = this.generics,
            target = this.target
        )
    }
}
