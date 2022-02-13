package com.spirytusz.booster.processor.kapt

import com.google.auto.service.AutoService
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.base.const.Keys.KEY_NULL_SAFE
import com.spirytusz.booster.processor.base.const.Keys.KEY_TYPE_ADAPTER_FACTORY_NAME
import com.spirytusz.booster.processor.base.data.config.TypeAdapterClassGenConfig
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.data.type.KtVariance
import com.spirytusz.booster.processor.base.extensions.asTypeName
import com.spirytusz.booster.processor.base.scan.ClassScanner
import com.spirytusz.booster.processor.check.ClassCheckerImpl
import com.spirytusz.booster.processor.gen.TypeAdapterClassGeneratorFactory
import com.spirytusz.booster.processor.gen.TypeAdapterFactoryClassGeneratorImpl
import com.spirytusz.booster.processor.kapt.log.KaptMessageLogger
import com.spirytusz.booster.processor.scan.kapt.KaptClassScanner
import com.spirytusz.booster.processor.scan.kapt.KmClassCacheHolder
import com.spirytusz.booster.processor.scan.kapt.data.IElementOwner
import com.spirytusz.booster.processor.scan.kapt.data.KaptKtType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class KaptBoosterProcessor : AbstractProcessor() {

    private var round = 0

    private val logger by lazy {
        KaptMessageLogger(processingEnv)
    }

    private val RoundEnvironment.boostAnnotatedClasses: Sequence<TypeElement>
        get() {
            return getElementsAnnotatedWith(Boost::class.java)
                .asSequence()
                .filter { it.kind == ElementKind.CLASS }
                .filterNot { processingEnv.elementUtils.getPackageOf(it).isUnnamed }
                .map {
                    it as TypeElement
                }
        }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Boost::class.java.name)
    }

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        logger.info("start process >>> round=${++round}")
        val start = System.currentTimeMillis()
        val kmClassCacheHolder = KmClassCacheHolder(logger)
        val classScanners = env.boostAnnotatedClasses.map {
            KaptClassScanner(processingEnv, it, kmClassCacheHolder, logger)
        }.toList()

        classScanners.forEach {
            ClassCheckerImpl(logger).check(it)
        }

        if (classScanners.isEmpty()) {
            logger.info("end process >>> round=$round timeCost = ${System.currentTimeMillis() - start}ms")
            return false
        }
        val classFilter = classScanners.map { it.classKtType }.toSet()

        val scannerToTypeSpecs = classScanners.map { classScanner ->
            val classType = classScanner.classKtType
            classScanner.ktFields.forEach {
                logger.info("${classType.toReadableString()} >>> ${it.toReadableString()}")
            }
            val typeAdapterClassGenerator = TypeAdapterClassGeneratorFactory
                .create(classFilter, logger)

            val typeAdapterClassGenConfig = TypeAdapterClassGenConfig(
                nullSafe = processingEnv.options[KEY_NULL_SAFE] == true.toString()
            )
            val typeSpec = typeAdapterClassGenerator.generate(
                classScanner,
                typeAdapterClassGenConfig
            ).toBuilder().apply {
                (classType as IElementOwner).target?.let { addOriginatingElement(it) }
            }.build()
            classScanner to typeSpec
        }

        scannerToTypeSpecs.map { (classScanner, typeSpec) ->
            val className = classScanner.classKtType.asTypeName() as ClassName
            FileSpec.builder(className.packageName, typeSpec.name.toString())
                .addType(typeSpec)
                .indent(" ".repeat(4))
                .build()
        }.forEach {
            it.writeTo(processingEnv.filer)
        }

        generateTypeAdapterFactoryIfNeed(scannerToTypeSpecs)
        logger.info("end process >>> round=$round timeCost = ${System.currentTimeMillis() - start}ms")

        return false
    }

    private fun generateTypeAdapterFactoryIfNeed(
        scannerToTypeSpecs: List<Pair<ClassScanner, TypeSpec>>
    ) {
        if (!processingEnv.options.containsKey(KEY_TYPE_ADAPTER_FACTORY_NAME)) {
            return
        }
        val typeAdapterFactoryName = processingEnv.options[KEY_TYPE_ADAPTER_FACTORY_NAME]!!
        if (typeAdapterFactoryName.isEmpty()) {
            throw IllegalArgumentException("Invalid $KEY_TYPE_ADAPTER_FACTORY_NAME param")
        }

        logger.info("generate typeAdapterFactory: $typeAdapterFactoryName")
        val classToTypeAdapters = scannerToTypeSpecs.map { (classScanner, typeSpec) ->
            val classKtType = classScanner.classKtType
            val typeAdapterPackageName = (classKtType.asTypeName() as ClassName).packageName
            val typeAdapterSimpleName = typeSpec.name.toString()
            val typeAdapterKtType = KaptKtType(
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
            .toBuilder().apply {
                classToTypeAdapters.mapNotNull { (classKtType, _) ->
                    (classKtType as? IElementOwner)?.target
                }.forEach { addOriginatingElement(it) }
            }.build()
        val typeAdapterFactoryClassName = ClassName.bestGuess(typeAdapterFactoryName)

        FileSpec.builder(
            typeAdapterFactoryClassName.packageName,
            typeAdapterFactoryClassName.simpleName
        ).addType(typeAdapterFactorySpec)
            .indent(" ".repeat(4))
            .build()
            .writeTo(processingEnv.filer)
    }

}