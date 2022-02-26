package com.spirytusz.booster.plugin.base.data

import com.intellij.psi.PsiElement

interface IPsiElementOwner {

    val target: PsiElement?
}