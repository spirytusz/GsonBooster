package com.spirytusz.booster.processor.ksp

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.ksp.log.KspMessageLogger
import com.spirytusz.booster.processor.scan.ksp.impl.KspClassScannerFactory

class KspBoosterProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private var round = 0

    private val logger by lazy { KspMessageLogger(environment) }

    private val Resolver.boostAnnotatedClasses: Sequence<KSClassDeclaration>
        get() = this.getSymbolsWithAnnotation(Boost::class.java.canonicalName)
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind == ClassKind.CLASS
            }.filter {
                it.getVisibility() == Visibility.PUBLIC
            }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("start process >>> round=${++round}")
        resolver.boostAnnotatedClasses.map {
            KspClassScannerFactory.create(environment, resolver, it, logger).ktFields
        }.flatten().toList().forEach {
            logger.info("ktField >>> ${it.toReadableString()}")
        }
        return emptyList()
    }
}