package com.zspirytus.booster.processor.data.type

import javax.lang.model.element.VariableElement

data class BackoffKType(
    val element: VariableElement
) : KType(element) {
    override val adapterFieldName: String
        get() = "backoffTypeAdapter"
}