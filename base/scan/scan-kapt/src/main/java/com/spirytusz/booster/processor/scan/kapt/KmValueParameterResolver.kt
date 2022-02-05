package com.spirytusz.booster.processor.scan.kapt

import com.google.gson.annotations.SerializedName
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.data.type.KtType
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.scan.kapt.data.KaptKtField
import kotlinx.metadata.Flag
import kotlinx.metadata.KmValueParameter
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement

class KmValueParameterResolver(
    private val processingEnvironment: ProcessingEnvironment,
    private val belongingClass: TypeElement,
    private val aptVariableElement: VariableElement?,
    private val kmValueParameter: KmValueParameter,
    private val logger: MessageLogger
) {

    fun resolveKmValueParameter(): KtField {
        val fieldName = kmValueParameter.name
        val isFinal = Flag.Property.IS_VAR(kmValueParameter.flags)
        val serializedNameAnnotation = aptVariableElement?.getAnnotation(SerializedName::class.java)
        val keys = if (serializedNameAnnotation != null) {
            val result = mutableListOf<String>()
            result.add(serializedNameAnnotation.value)
            result.addAll(serializedNameAnnotation.alternate)
            result.toList()
        } else {
            listOf(fieldName)
        }
        val initializer = if (Flag.ValueParameter.DECLARES_DEFAULT_VALUE(kmValueParameter.flags)) {
            FieldInitializer.HAS_DEFAULT
        } else {
            FieldInitializer.NONE
        }
        val ktType = resolveKtType()
        return KaptKtField(
            isFinal = isFinal,
            fieldName = fieldName,
            keys = keys,
            ktType = ktType,
            initializer = initializer,
            transient = aptVariableElement?.modifiers?.contains(Modifier.TRANSIENT) == true,
            declarationScope = DeclarationScope.PRIMARY_CONSTRUCTOR,
            target = aptVariableElement
        )
    }

    private fun resolveKtType(): KtType {
        val kmType = kmValueParameter.type ?: kotlin.run {
            logger.error(
                "Unexpected constructor param type",
                aptVariableElement
            )
            throw IllegalStateException("Unexpected constructor param type ${belongingClass.qualifiedName}.${aptVariableElement?.simpleName}")
        }
        return KmTypeResolver(
            processingEnvironment,
            aptVariableElement,
            kmType
        ).resolvedKtType
    }
}