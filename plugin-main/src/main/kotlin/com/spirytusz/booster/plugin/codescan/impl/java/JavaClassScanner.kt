package com.spirytusz.booster.plugin.codescan.impl.java

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.spirytusz.booster.plugin.base.data.PsiFieldDescriptor
import com.spirytusz.booster.plugin.codescan.resolve.StringExpressionResolver
import com.spirytusz.booster.plugin.codescan.const.Const.Types.SERIALIZED_NAME
import com.spirytusz.booster.plugin.codescan.impl.AbstractClassScanner
import com.spirytusz.booster.plugin.codescan.impl.java.extensions.filterModifier
import com.spirytusz.booster.plugin.codescan.impl.java.extensions.hasModifierStable
import com.spirytusz.booster.plugin.codescan.impl.java.extensions.toPsiTypeDescriptor
import com.spirytusz.booster.plugin.codescan.impl.java.extensions.typeOrThrow
import com.spirytusz.booster.plugin.codescan.impl.java.resolve.JavaStringExpressionResolver
import com.spirytusz.booster.processor.base.data.BoosterClassKind
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.type.KtType

class JavaClassScanner(private val psiClass: PsiClass) : AbstractClassScanner() {

    private class JavaAnnotationValueException(msg: String) : IllegalArgumentException(msg)

    override val classKind: BoosterClassKind by lazy {
        resolveClassKind()
    }

    override val classKtType: KtType by lazy {
        psiClass.toPsiTypeDescriptor()
    }

    override val superTypes by lazy {
        JavaClassSuperTypeScanner(psiClass).superTypes
    }

    private fun resolveClassKind(): BoosterClassKind {
        return when {
            psiClass.isInterface -> BoosterClassKind.INTERFACE
            psiClass.isEnum -> BoosterClassKind.ENUM_CLASS
            psiClass.isAnnotationType -> BoosterClassKind.ANNOTATION
            else -> BoosterClassKind.CLASS
        }
    }

    override fun scanPrimaryConstructorProperties(): List<PsiFieldDescriptor> {
        return listOf()
    }

    override fun scanClassProperties(): List<PsiFieldDescriptor> {
        return psiClass.fields.toList().filterModifier().map {
            createPsiFieldDescriptorFromPsiField(it)
        }.toList()
    }

    override fun scanSuperProperties(): List<PsiFieldDescriptor> {
        return superTypes.scanPsiFieldDescriptors().toList()
    }

    private fun createPsiFieldDescriptorFromPsiField(psiField: PsiField): PsiFieldDescriptor {
        return PsiFieldDescriptor(
            isFinal = psiField.hasModifierStable("final"),
            fieldName = psiField.name,
            keys = resolveSerializedNames(psiField).map { it.combinedExpression }.ifEmpty { listOf(psiField.name) },
            ktType = psiField.typeOrThrow().toPsiTypeDescriptor(),
            initializer = FieldInitializer.HAS_DEFAULT,
            transient = psiField.hasModifierStable("transient"),
            declarationScope = DeclarationScope.BODY,
            target = psiField
        )
    }

    private fun resolveSerializedNames(psiField: PsiField): List<StringExpressionResolver> {
        val serializedName = psiField.annotations.find {
            it.qualifiedName == SERIALIZED_NAME
        } ?: return emptyList()

        return serializedName.parameterList.attributes.map {
            val annotationValue = it.value ?: throw JavaAnnotationValueException(psiField.text)
            JavaStringExpressionResolver(annotationValue)
        }
    }
}