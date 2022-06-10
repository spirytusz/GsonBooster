package com.spirytusz.booster.processor.scan.kapt.data

import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtType
import javax.lang.model.element.Element

data class KaptKtField(
    override val isFinal: Boolean,
    override val fieldName: String,
    override val keys: List<String>,
    override val ktType: KtType,
    override val initializer: FieldInitializer,
    override val declarationScope: DeclarationScope,
    override val transient: Boolean,
    override val target: Element?
) : KtField, IElementOwner {

    override fun copy(declarationScope: DeclarationScope): KtField {
        return KaptKtField(
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