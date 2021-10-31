package com.spirytusz.booster.processor.gen

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.spirytusz.booster.processor.gen.api.BoosterCodeGenerator
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

class BoosterCodegenProcessor(private val environment: SymbolProcessorEnvironment) {

    @KotlinPoetKspPreview
    fun process(classScanners: Set<AbstractClassScanner>) {
        val groupByFile: Map<KSFile, Set<AbstractClassScanner>> = classScanners.groupBy {
            it.containingFile
        }.mapValues {
            it.value.toSet()
        }
        groupByFile.forEach { (ksFile, ksFileClassScanners) ->
            BoosterCodeGenerator(
                environment = environment,
                ksFile = ksFile,
                allClassScanners = classScanners,
                ksFileClassScanners = ksFileClassScanners
            ).process()
        }
    }
}