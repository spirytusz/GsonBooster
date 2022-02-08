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
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.gen.TypeAdapterClassGeneratorFactory
import com.spirytusz.booster.processor.ksp.log.KspMessageLogger
import com.spirytusz.booster.processor.scan.ksp.KspClassScannerFactory
import com.spirytusz.booster.processor.scan.ksp.data.IKsNodeOwner
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

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
        val classScanners = resolver.boostAnnotatedClasses.map {
            KspClassScannerFactory.create(environment, resolver, it, logger)
        }.toList()
        val classFilter = classScanners.map { it.classKtType }.toSet()

        classScanners.map { classScanner ->
            val classType = classScanner.classKtType
            logger.info("classType >>> ${classType.toReadableString()}")
            val className = classType.asTypeName() as ClassName
            val typeAdapterClassGenerator = TypeAdapterClassGeneratorFactory
                .create(classFilter, logger)
            val typeSpec = typeAdapterClassGenerator.generate(classScanner)
            val fileSpec = FileSpec.builder(className.packageName, className.simpleName)
                .addType(typeSpec)
                .indent(" ".repeat(4))
                .build()
            classType to fileSpec
        }.forEach { (classType, fileSpec) ->
            val ksFile = ((classType as IKsNodeOwner).target as? KSClassDeclaration)?.containingFile
            val originatingKSFiles = if (ksFile != null) {
                listOf(ksFile)
            } else {
                emptyList()
            }
            fileSpec.writeTo(
                codeGenerator = environment.codeGenerator,
                aggregating = false,
                originatingKSFiles = originatingKSFiles
            )
        }
        return emptyList()
    }
}