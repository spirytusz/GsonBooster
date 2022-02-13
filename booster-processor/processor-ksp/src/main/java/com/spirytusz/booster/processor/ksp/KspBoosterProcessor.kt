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
import com.spirytusz.booster.processor.base.const.Keys
import com.spirytusz.booster.processor.base.const.Keys.KEY_TYPE_ADAPTER_FACTORY_NAME
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.check.ClassCheckerImpl
import com.spirytusz.booster.processor.gen.TypeAdapterClassGeneratorFactory
import com.spirytusz.booster.processor.gen.TypeAdapterFactoryClassGeneratorImpl
import com.spirytusz.booster.processor.ksp.log.KspMessageLogger
import com.spirytusz.booster.processor.scan.ksp.KspClassScannerFactory
import com.spirytusz.booster.processor.scan.ksp.data.IKsNodeOwner
import com.spirytusz.booster.processor.scan.ksp.data.KspKtType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
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
        val start = System.currentTimeMillis()
        val classScanners = resolver.boostAnnotatedClasses.map {
            KspClassScannerFactory.create(environment, resolver, it, logger)
        }.toList()

        classScanners.forEach {
            ClassCheckerImpl(logger).check(it)
        }

        if (classScanners.isEmpty()) {
            logger.info("end process >>> round=$round timeCost = ${System.currentTimeMillis() - start}ms")
            return emptyList()
        }

        val classFilter = classScanners.map { it.classKtType }.toSet()
        val scannerToTypeSpecs = classScanners.map { classScanner ->
            val classType = classScanner.classKtType
            classScanner.ktFields.forEach {
                logger.info(" ${classType.toReadableString()} >>> ${it.toReadableString()}")
            }
            val typeAdapterClassGenerator = TypeAdapterClassGeneratorFactory
                .create(classFilter, logger)
            val typeAdapterClassGenConfig = TypeAdapterClassGenConfig(
                nullSafe = environment.options[Keys.KEY_NULL_SAFE] == true.toString()
            )
            val typeSpec =
                typeAdapterClassGenerator.generate(classScanner, typeAdapterClassGenConfig)

            classScanner to typeSpec
        }

        scannerToTypeSpecs.forEach { (classScanner, typeSpec) ->
            val classType = classScanner.classKtType
            val className = classType.asTypeName() as ClassName

            val fileSpec = FileSpec.builder(className.packageName, typeSpec.name.toString())
                .addType(typeSpec)
                .indent(" ".repeat(4))
                .build()

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

        generateTypeAdapterFactoryIfNeed(scannerToTypeSpecs)
        logger.info("end process >>> round=$round timeCost = ${System.currentTimeMillis() - start}ms")

        return emptyList()
    }

    private fun generateTypeAdapterFactoryIfNeed(
        scannerToTypeSpecs: List<Pair<ClassScanner, TypeSpec>>
    ) {
        if (!environment.options.containsKey(KEY_TYPE_ADAPTER_FACTORY_NAME)) {
            return
        }
        val typeAdapterFactoryName = environment.options[KEY_TYPE_ADAPTER_FACTORY_NAME]!!
        if (typeAdapterFactoryName.isEmpty()) {
            throw IllegalArgumentException("Invalid $KEY_TYPE_ADAPTER_FACTORY_NAME param")
        }

        logger.info("generate typeAdapterFactory: $typeAdapterFactoryName")
        val classToTypeAdapters = scannerToTypeSpecs.map { (classScanner, typeSpec) ->
            val classKtType = classScanner.classKtType
            val typeAdapterPackageName = (classKtType.asTypeName() as ClassName).packageName
            val typeAdapterSimpleName = typeSpec.name.toString()
            val typeAdapterKtType = KspKtType(
                rawType = ClassName(typeAdapterPackageName, typeAdapterSimpleName).canonicalName,
                nullable = false,
                variance = KtVariance.INVARIANT,
                jsonTokenName = JsonTokenName.OBJECT,
                generics = listOf(),
                target = null
            )
            classKtType to typeAdapterKtType
        }.toSet()

        val typeAdapterFactorySpec = TypeAdapterFactoryClassGeneratorImpl(logger)
            .generate(typeAdapterFactoryName, classToTypeAdapters)
        val typeAdapterFactoryClassName = ClassName.bestGuess(typeAdapterFactoryName)
        val originatingKSFiles = classToTypeAdapters.mapNotNull { (classKtType, _) ->
            (classKtType as? IKsNodeOwner)?.target as? KSClassDeclaration
        }.mapNotNull {
            it.containingFile
        }

        FileSpec.builder(
            typeAdapterFactoryClassName.packageName,
            typeAdapterFactoryClassName.simpleName
        ).addType(typeAdapterFactorySpec)
            .indent(" ".repeat(4))
            .build()
            .writeTo(
                codeGenerator = environment.codeGenerator,
                aggregating = false,
                originatingKSFiles = originatingKSFiles
            )
    }
}