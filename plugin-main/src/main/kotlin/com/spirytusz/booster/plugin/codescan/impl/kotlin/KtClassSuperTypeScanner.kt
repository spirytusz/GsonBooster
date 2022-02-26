package com.spirytusz.booster.plugin.codescan.impl.kotlin

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.spirytusz.booster.plugin.codescan.impl.java.JavaClassSuperTypeScanner
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.source.getPsi

class KtClassSuperTypeScanner(private val ktClass: KtClassOrObject) {

    val superTypes: Set<PsiElement> by lazy {
        scanSuperTypes()
    }

    private fun scanSuperTypes(): Set<PsiElement> {
        if (ktClass.superTypeListEntries.isEmpty()) {
            return setOf()
        }

        val directSuperTypes = ktClass.superTypeListEntries.asSequence().mapNotNull {
            it.typeReference?.findSourceDeclaration()
        }.toSet()

        return directSuperTypes + directSuperTypes.asSequence().map { psiElement ->
            if (psiElement is KtClass) {
                KtClassSuperTypeScanner(psiElement).superTypes
            } else {
                JavaClassSuperTypeScanner(psiElement as PsiClass).superTypes
            }
        }.flatten().distinctBy {
            if (it is KtClass) {
                it.fqName?.toString()
            } else {
                (it as PsiClass).qualifiedName.toString()
            }
        }.toSet()
    }

    private fun KtTypeReference.findSourceDeclaration(): PsiElement? {
        val bindingContext = this.analyze()
        val kotlinType = bindingContext.get(BindingContext.TYPE, this)
        val declarationDescriptor = kotlinType?.constructor?.declarationDescriptor
        return declarationDescriptor?.source?.getPsi()
    }
}