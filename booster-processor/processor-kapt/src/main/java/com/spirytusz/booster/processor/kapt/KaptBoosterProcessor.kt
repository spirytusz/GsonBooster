package com.spirytusz.booster.processor.kapt

import com.google.auto.service.AutoService
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.kapt.log.KaptMessageLogger
import com.spirytusz.booster.processor.scan.kapt.KaptClassScanner
import com.spirytusz.booster.processor.scan.kapt.KmClassCacheHolder
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
        val kmClassCacheHolder = KmClassCacheHolder(logger)

        env.boostAnnotatedClasses.map {
            KaptClassScanner(processingEnv, it, kmClassCacheHolder, logger)
        }.map {
            logger.info("classType >>> ${it.classKtType.toReadableString()}")
            it.ktFields
        }.toList().flatten().forEach {
            logger.info("ktField >>> ${it.toReadableString()}")
        }
        return false
    }

}