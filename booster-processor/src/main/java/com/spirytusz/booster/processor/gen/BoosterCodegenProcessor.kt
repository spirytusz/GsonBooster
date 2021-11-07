package com.spirytusz.booster.processor.gen

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import com.spirytusz.booster.processor.config.BoosterGenConfig
import com.spirytusz.booster.processor.gen.api.BoosterCodeGenerator
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner

class BoosterCodegenProcessor(private val environment: SymbolProcessorEnvironment) {

    fun process(classScanners: Set<AbstractClassScanner>) {
        val groupByFile: Map<KSFile, Set<AbstractClassScanner>> = classScanners.groupBy {
            it.containingFile
        }.mapValues {
            it.value.toSet()
        }
        val config = readBoosterGenConfig()
        groupByFile.forEach { (ksFile, ksFileClassScanners) ->
            BoosterCodeGenerator(
                environment = environment,
                ksFile = ksFile,
                allClassScanners = classScanners,
                ksFileClassScanners = ksFileClassScanners,
                config = config
            ).process()
        }
        BoosterTypeAdapterFactoryGenerator(environment).generate(classScanners, config)
    }

    private fun readBoosterGenConfig(): BoosterGenConfig {
        val default = BoosterGenConfig()
        val fromJsonNullSafe = environment.options["nullSafe"]?.toBooleanStrictOrNull()
        val typeAdapterFactoryName = environment.options["factoryName"]
        return BoosterGenConfig(
            fromJsonNullSafe = fromJsonNullSafe ?: default.fromJsonNullSafe,
            typeAdapterFactoryName = typeAdapterFactoryName ?: default.typeAdapterFactoryName
        )
    }
}