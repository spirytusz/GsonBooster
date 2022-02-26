package com.spirytusz.booster.plugin.codescan.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.spirytusz.booster.plugin.base.data.PsiFieldDescriptor
import com.spirytusz.booster.plugin.codescan.impl.java.JavaClassScanner
import com.spirytusz.booster.plugin.codescan.impl.kotlin.KtClassScanner
import com.spirytusz.booster.processor.base.data.DeclarationScope
import com.spirytusz.booster.processor.base.data.KtField
import com.spirytusz.booster.processor.base.scan.ClassScanner
import org.jetbrains.kotlin.psi.KtClass

abstract class AbstractClassScanner : ClassScanner {

    final override val ktFields: List<KtField> by lazy {
        scanPrimaryConstructorProperties() + scanClassProperties() + scanSuperProperties()
    }

    abstract val superTypes: Set<PsiElement>

    internal abstract fun scanPrimaryConstructorProperties(): List<PsiFieldDescriptor>

    internal abstract fun scanClassProperties(): List<PsiFieldDescriptor>

    internal abstract fun scanSuperProperties(): List<PsiFieldDescriptor>

    protected fun Set<PsiElement>.scanPsiFieldDescriptors(): Sequence<PsiFieldDescriptor> {
        val javaSuperFields = asSequence().filterIsInstance<PsiClass>().filter {
            !it.isEnum
        }.map { psiClass ->
            val declarationScope = if (psiClass.isInterface) {
                DeclarationScope.SUPER_INTERFACE
            } else {
                DeclarationScope.SUPER_CLASS
            }
            JavaClassScanner(psiClass).ktFields.map { it.copy(declarationScope = declarationScope) }
        }.flatten().filterIsInstance<PsiFieldDescriptor>()

        val ktSuperFields = asSequence().filterIsInstance<KtClass>().filter {
            !it.isEnum()
        }.map { ktClass ->
            val declarationScope = if (ktClass.isInterface()) {
                DeclarationScope.SUPER_INTERFACE
            } else {
                DeclarationScope.SUPER_CLASS
            }
            KtClassScanner(ktClass).ktFields.map { it.copy(declarationScope = declarationScope) }
        }.flatten().filterIsInstance<PsiFieldDescriptor>()

        return javaSuperFields + ktSuperFields
    }
}