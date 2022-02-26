package com.spirytusz.booster.plugin.codescan.impl.java

import com.intellij.lang.java.JavaLanguage
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.spirytusz.booster.plugin.base.data.FileScanner
import com.spirytusz.booster.plugin.codescan.extensions.getChildOfTypeAsList
import com.spirytusz.booster.processor.base.scan.ClassScanner
import org.jetbrains.uast.UFile
import org.jetbrains.uast.toUElement

class JavaFileScanner(private val psiFile: PsiJavaFile) : FileScanner {
    override val sourceFile: UFile by lazy {
        psiFile.toUElement() as UFile
    }

    override val classScanners: Set<ClassScanner> by lazy {
        getAllClassScanners()
    }

    private fun getAllClassScanners(): Set<ClassScanner> {
        return psiFile.getChildOfTypeAsList<PsiClass>().filterNot {
            it.language !is JavaLanguage || it.isEnum || it.isInterface
        }.map {
            JavaClassScanner(it)
        }.toSet()
    }
}