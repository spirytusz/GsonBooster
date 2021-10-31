package com.spirytusz.booster.processor.base

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility
import com.spirytusz.booster.annotation.Boost

abstract class BaseSymbolProcessor(environment: SymbolProcessorEnvironment) : SymbolProcessor {

    protected val logger by lazy {
        environment.logger
    }

    protected val Resolver.boostAnnotatedClasses: Set<KSClassDeclaration>
        get() = this.getSymbolsWithAnnotation(Boost::class.java.canonicalName)
            .filterIsInstance<KSClassDeclaration>()
            .filter {
                it.classKind == ClassKind.CLASS
            }.filter {
                it.getVisibility() == Visibility.PUBLIC
            }
            .toSet()
}