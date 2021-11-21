package com.spirytusz.booster.processor.scan

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.scan.api.AbstractClassScanner
import com.spirytusz.booster.processor.scan.factory.ClassScannerFactory

class ClassScanProcessor(
    private val resolver: Resolver,
    private val environment: SymbolProcessorEnvironment
) {

    companion object {
        private val TARGET_ANNOTATION = Boost::class.qualifiedName!!
    }

    private val Resolver.boostAnnotatedClasses: Set<KSClassDeclaration>
        get() = this.getSymbolsWithAnnotation(TARGET_ANNOTATION)
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind == ClassKind.CLASS
            }.filter {
                it.getVisibility() == Visibility.PUBLIC
            }
            .toSet()

    fun process(): Set<AbstractClassScanner> {
        val boostAnnotatedClasses = resolver.boostAnnotatedClasses
        if (boostAnnotatedClasses.isEmpty()) {
            environment.logger.warn("No $TARGET_ANNOTATION annotated class found")
            return emptySet()
        }
        return resolver.boostAnnotatedClasses.map { boostAnnotatedClass ->
            ClassScannerFactory.createClassScanner(resolver, environment, boostAnnotatedClass)
        }.toSet()
    }
}