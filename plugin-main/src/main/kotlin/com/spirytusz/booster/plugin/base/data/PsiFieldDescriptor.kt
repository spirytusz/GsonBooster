package com.spirytusz.booster.plugin.base.data

import com.intellij.psi.PsiElement
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtType

data class PsiFieldDescriptor(
    override val isFinal: Boolean,
    override val fieldName: String,
    override val keys: List<String>,
    override val ktType: KtType,
    override val initializer: FieldInitializer,
    override val declarationScope: DeclarationScope,
    override val transient: Boolean,
    override val target: PsiElement?
) : KtField, IPsiElementOwner {

    override fun copy(declarationScope: DeclarationScope): KtField {
        return PsiFieldDescriptor(
            isFinal = isFinal,
            fieldName = fieldName,
            keys = keys,
            ktType = ktType,
            initializer = initializer,
            declarationScope = declarationScope,
            transient = transient,
            target = target
        )
    }
}