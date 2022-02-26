package com.spirytusz.booster.plugin.codescan.impl.java

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.psi.KtClass

class JavaClassSuperTypeScanner(private val psiClass: PsiClass) {

    val superTypes: Set<PsiElement> by lazy {
        scanSuperTypes()
    }

    private fun scanSuperTypes(): Set<PsiElement> {
        val superClass = psiClass.superClass ?: return emptySet()
        if (superClass is KtUltraLightClass) {
            val ktClass = superClass.kotlinOrigin as? KtClass ?: return emptySet()
            return setOf(ktClass)
        } else {
            return setOf(superClass)
        }
    }
}