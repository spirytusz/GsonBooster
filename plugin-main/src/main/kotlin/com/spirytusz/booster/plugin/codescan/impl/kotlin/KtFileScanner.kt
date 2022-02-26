package com.spirytusz.booster.plugin.codescan.impl.kotlin

import com.intellij.psi.PsiFile
import com.spirytusz.booster.plugin.base.data.FileScanner
import com.spirytusz.booster.plugin.codescan.extensions.getChildOfTypeAsList
import com.spirytusz.booster.processor.base.scan.ClassScanner
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.uast.UFile
import org.jetbrains.uast.toUElement

class KtFileScanner(private val ktFile: PsiFile) : FileScanner {

    override val sourceFile: UFile by lazy {
        ktFile.toUElement() as UFile
    }

    override val classScanners: Set<ClassScanner> by lazy {
        getAllClassScanners()
    }

    private fun getAllClassScanners(): Set<ClassScanner> {
        return ktFile.getChildOfTypeAsList<KtClass>().filterNot {
            it.isEnum() || it.isInterface()
        }.map {
            KtClassScanner(it)
        }.toSet()
    }
}