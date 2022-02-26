package com.spirytusz.booster.plugin.codescan

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.spirytusz.booster.plugin.base.data.FileScanner
import com.spirytusz.booster.plugin.codescan.impl.java.JavaFileScanner
import com.spirytusz.booster.plugin.codescan.impl.kotlin.KtFileScanner
import org.jetbrains.kotlin.idea.KotlinLanguage

object FileScannerFactory {

    fun create(psiFile: PsiFile): FileScanner {
        return when (psiFile.language) {
            is JavaLanguage -> JavaFileScanner(psiFile as PsiJavaFile)
            is KotlinLanguage -> KtFileScanner(psiFile)
            else -> {
                throw IllegalArgumentException("Unsupported psiElement language ${psiFile.language}")
            }
        }
    }
}