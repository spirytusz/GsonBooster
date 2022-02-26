package com.spirytusz.booster.plugin.base.data

import com.intellij.psi.PsiElement
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance

data class PsiTypeDescriptor(
    override val rawType: String,
    override val nullable: Boolean,
    override val variance: KtVariance,
    override val jsonTokenName: JsonTokenName,
    override val generics: List<KtType>,
    override val target: PsiElement?
) : KtType, IPsiElementOwner {

    override fun copy(nullable: Boolean, variance: KtVariance): KtType {
        return PsiTypeDescriptor(
            rawType = rawType,
            nullable = nullable,
            variance = variance,
            jsonTokenName = jsonTokenName,
            generics = generics,
            target = target
        )
    }
}