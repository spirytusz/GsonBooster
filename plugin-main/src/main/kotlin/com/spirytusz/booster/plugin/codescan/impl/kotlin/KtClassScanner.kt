package com.spirytusz.booster.plugin.codescan.impl.kotlin

import com.spirytusz.booster.plugin.base.data.PsiFieldDescriptor
import com.spirytusz.booster.plugin.codescan.const.Const.Types.SERIALIZED_NAME
import com.spirytusz.booster.plugin.codescan.impl.AbstractClassScanner
import com.spirytusz.booster.plugin.codescan.impl.kotlin.extensions.toPsiTypeDescriptor
import com.spirytusz.booster.plugin.codescan.impl.kotlin.extensions.tryGetAnnotation
import com.spirytusz.booster.plugin.codescan.impl.kotlin.extensions.typeOrThrow
import com.spirytusz.booster.plugin.codescan.impl.kotlin.resolve.KtStringExpressionResolver
import com.spirytusz.booster.processor.base.data.BoosterClassKind
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.FieldInitializer
import com.spirytusz.booster.processor.base.data.type.KtType
import org.jetbrains.kotlin.idea.refactoring.changeSignature.KotlinValVar
import org.jetbrains.kotlin.idea.refactoring.changeSignature.toValVar
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierTypeOrDefault

class KtClassScanner(private val ktClass: KtClassOrObject) : AbstractClassScanner() {

    override val classKind: BoosterClassKind by lazy {
        resolveClassKind()
    }

    override val classKtType: KtType by lazy {
        ktClass.toPsiTypeDescriptor()
    }

    override val superTypes by lazy {
        KtClassSuperTypeScanner(ktClass).superTypes
    }

    private fun resolveClassKind(): BoosterClassKind {
        return when (ktClass) {
            is KtClass -> {
                when {
                    ktClass.hasModifier(KtTokens.ANNOTATION_KEYWORD) -> BoosterClassKind.ANNOTATION
                    ktClass.isInterface() -> BoosterClassKind.INTERFACE
                    ktClass.isEnum() -> BoosterClassKind.ENUM_CLASS
                    else -> BoosterClassKind.CLASS
                }
            }
            else -> BoosterClassKind.OBJECT
        }
    }

    override fun scanPrimaryConstructorProperties(): List<PsiFieldDescriptor> {
        val primaryConstructorValueParameters = ktClass.primaryConstructor?.valueParameters ?: return emptyList()
        return primaryConstructorValueParameters.filter { ktDeclaration ->
            ktDeclaration.visibilityModifierTypeOrDefault() == KtTokens.PUBLIC_KEYWORD
        }.map { ktParameter ->
            createPsiFieldDescriptorFromDeclaration(ktParameter)
        }
    }

    override fun scanClassProperties(): List<PsiFieldDescriptor> {
        return (ktClass as? KtClass)?.getProperties()?.filter { ktDeclaration ->
            ktDeclaration.visibilityModifierTypeOrDefault() == KtTokens.PUBLIC_KEYWORD
        }?.map { ktProperty ->
            createPsiFieldDescriptorFromDeclaration(ktProperty)
        } ?: emptyList()
    }

    override fun scanSuperProperties(): List<PsiFieldDescriptor> {
        return superTypes.scanPsiFieldDescriptors().toList()
    }

    private fun createPsiFieldDescriptorFromDeclaration(ktDeclaration: KtDeclaration): PsiFieldDescriptor {
        val isFinal = if (ktDeclaration is KtValVarKeywordOwner) {
            when (ktDeclaration.valOrVarKeyword.toValVar()) {
                KotlinValVar.Val -> true
                KotlinValVar.Var -> false
                else -> false
            }
        } else {
            false
        }

        val type = ktDeclaration.typeOrThrow().toPsiTypeDescriptor()

        val initializer = when (ktDeclaration) {
            is KtParameter -> {
                if (ktDeclaration.hasDefaultValue()) {
                    FieldInitializer.HAS_DEFAULT
                } else {
                    FieldInitializer.NONE
                }
            }
            is KtProperty -> {
                if (ktDeclaration.hasDelegate()) {
                    FieldInitializer.DELEGATED
                } else {
                    FieldInitializer.HAS_DEFAULT
                }
            }
            else -> {
                FieldInitializer.NONE
            }
        }

        val declarationScope = if (ktDeclaration is KtParameter) {
            DeclarationScope.PRIMARY_CONSTRUCTOR
        } else {
            DeclarationScope.BODY
        }

        val annotationKeys = ktDeclaration.getSerializedNameKeys().map { it.combinedExpression }
            .ifEmpty { listOf(ktDeclaration.name.toString()) }

        return PsiFieldDescriptor(
            isFinal = isFinal,
            fieldName = ktDeclaration.name.toString(),
            ktType = type,
            keys = annotationKeys,
            initializer = initializer,
            transient = ktDeclaration.tryGetAnnotation(Transient::class.qualifiedName.toString()) != null,
            declarationScope = declarationScope,
            target = ktDeclaration
        )
    }

    private fun <T : KtDeclaration> T.getSerializedNameKeys(): List<KtStringExpressionResolver> {
        val serializedNameAnnotation = tryGetAnnotation(SERIALIZED_NAME) ?: return emptyList()

        fun KtExpression.toStringExpressionResolvers(): List<KtStringExpressionResolver> {
            return if (this is KtCollectionLiteralExpression) {
                this.getInnerExpressions().map { ktInnerExpression ->
                    KtStringExpressionResolver(ktInnerExpression)
                }
            } else {
                listOf(KtStringExpressionResolver(this))
            }
        }

        return serializedNameAnnotation.valueArguments.asSequence().mapNotNull {
            it.getArgumentExpression()
        }.map { ktExpression ->
            ktExpression.toStringExpressionResolvers()
        }.flatten().toList()
    }
}