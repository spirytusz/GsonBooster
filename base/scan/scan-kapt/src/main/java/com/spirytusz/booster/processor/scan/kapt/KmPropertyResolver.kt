package com.spirytusz.booster.processor.scan.kapt

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.scan.kapt.data.KaptKtField
import kotlinx.metadata.Flag
import kotlinx.metadata.KmProperty
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class KmPropertyResolver(
    private val processingEnvironment: ProcessingEnvironment,
    private val belongingClass: TypeElement,
    private val aptVariableElement: VariableElement?,
    private val kmProperty: KmProperty
) {

    fun resolveKmProperty(): KtField {
        val fieldName = kmProperty.name
        val isFinal = !Flag.Property.IS_VAR(kmProperty.flags)
        val serializedNameAnnotation = aptVariableElement?.getAnnotation(SerializedName::class.java)
        val keys = if (serializedNameAnnotation != null) {
            val result = mutableListOf<String>()
            result.add(serializedNameAnnotation.value)
            result.addAll(serializedNameAnnotation.alternate)
            result.toList()
        } else {
            listOf(fieldName)
        }
        val initializer = FieldInitializer.HAS_DEFAULT
        val ktType = resolveKtType()
        return KaptKtField(
            isFinal = isFinal,
            fieldName = fieldName,
            keys = keys,
            ktType = ktType,
            initializer = initializer,
            transient = aptVariableElement?.modifiers?.contains(Modifier.TRANSIENT) == true,
            declarationScope = DeclarationScope.BODY,
            target = aptVariableElement
        )
    }

    private fun resolveKtType(): KtType {
        return KmTypeResolver(
            processingEnvironment,
            aptVariableElement,
            kmProperty.returnType
        ).resolvedKtType
    }
}