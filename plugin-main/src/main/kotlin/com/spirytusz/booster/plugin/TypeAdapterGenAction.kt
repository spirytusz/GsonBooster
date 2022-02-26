package com.spirytusz.booster.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiFile
import com.spirytusz.booster.plugin.base.log.PsiMessageLogger
import com.spirytusz.booster.plugin.codescan.FileScannerFactory
import com.spirytusz.booster.processor.base.check.ClassChecker
import com.spirytusz.booster.processor.base.gen.TypeAdapterGenerator
import com.spirytusz.booster.processor.base.utils.ServiceManager
import com.squareup.kotlinpoet.FileSpec
import org.jetbrains.kotlin.psi.KtPsiFactory

class TypeAdapterGenAction : AnAction() {

    private val logger = PsiMessageLogger()

    override fun actionPerformed(e: AnActionEvent) {
        val psiFile = e.getData(PlatformDataKeys.PSI_FILE) ?: return

        val fileScanner = FileScannerFactory.create(psiFile)
        fileScanner.classScanners.forEach { classScanner ->
            classScanner.ktFields.forEach { ktField ->
                logger.info("${classScanner.classKind} ${classScanner.classKtType.toReadableString()} ${ktField.toReadableString()}")
            }
        }

        val classChecker = ServiceManager.fetchService<ClassChecker.Factory>().create(logger)
        fileScanner.classScanners.forEach { classScanner ->
            classChecker.check(classScanner)
        }

        val typeSpecs = fileScanner.classScanners.map { classScanner ->
            ServiceManager.fetchService<TypeAdapterGenerator.Factory>()
                .create(logger)
                .generate(classScanner, setOf())
        }

        val fileSpec = FileSpec.builder(fileScanner.sourceFile.packageName, psiFile.name)
        typeSpecs.forEach {
            fileSpec.addType(it)
        }

        val generatePsiFile =
            KtPsiFactory(psiFile.project).createFile(psiFile.nameToTypeAdapterFile(), fileSpec.build().toString())
        psiFile.containingDirectory?.add(generatePsiFile)
    }

    private fun PsiFile.nameToTypeAdapterFile(): String {
        val name = name.replace(".kt", "").replace(".java", "")
        return name + "TypeAdapter" + ".kt"
    }
}