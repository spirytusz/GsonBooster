package com.spirytusz.booster.plugin.base.data

import com.intellij.psi.PsiElement
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.data.type.KtVariance

data class PsiTypeVarianceDescriptor(
    override val rawType: String,
    override val variance: KtVariance,
    override val target: PsiElement?
) : KtType, IPsiElementOwner {

    override val nullable: Boolean = false

    override val jsonTokenName: JsonTokenName = JsonTokenName.OBJECT

    override val generics: List<KtType> = listOf()

    override fun copy(nullable: Boolean, variance: KtVariance): KtType {
        throw IllegalArgumentException("TypeVariance not support to copy")
    }
}