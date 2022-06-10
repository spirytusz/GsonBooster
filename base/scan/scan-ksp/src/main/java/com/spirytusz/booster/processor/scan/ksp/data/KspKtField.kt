package com.spirytusz.booster.processor.scan.ksp.data

import com.google.devtools.ksp.symbol.KSNode
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtType

data class KspKtField(
    override val isFinal: Boolean,
    override val fieldName: String,
    override val keys: List<String>,
    override val ktType: KtType,
    override val initializer: FieldInitializer,
    override val declarationScope: DeclarationScope,
    override val transient: Boolean,
    override val target: KSNode?
) : KtField, IKsNodeOwner {
    override fun copy(declarationScope: DeclarationScope): KtField {
        return KspKtField(
            isFinal = this.isFinal,
            fieldName = this.fieldName,
            keys = this.keys,
            ktType = this.ktType,
            initializer = this.initializer,
            declarationScope = declarationScope,
            transient = this.transient,
            target = this.target
        )
    }
}