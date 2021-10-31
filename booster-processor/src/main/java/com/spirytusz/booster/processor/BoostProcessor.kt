package com.spirytusz.booster.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.base.BaseSymbolProcessor
import com.spirytusz.booster.processor.scan.ClassScannerFactory

/**
 * [Boost]注解处理器
 *
 * 负责：
 *     1. 扫描获取所有被[Boost]注解的类
 *     2. 生成TypeAdapter
 *     3. 生成TypeAdapterFactory
 */
class BoostProcessor(
    private val environment: SymbolProcessorEnvironment
) : BaseSymbolProcessor(environment) {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.boostAnnotatedClasses.forEach { boostAnnotatedClass ->
            val classScanner = ClassScannerFactory
                .createClassScanner(resolver, environment, boostAnnotatedClass)
            classScanner.primaryConstructorProperties.forEach {
                logger.warn("${boostAnnotatedClass.simpleName.asString()} [${classScanner::class.java.simpleName}] primaryConstructorProperties $it")
            }
            classScanner.classProperties.forEach {
                logger.warn("${boostAnnotatedClass.simpleName.asString()} [${classScanner::class.java.simpleName}] classProperties $it")
            }
        }
        return emptyList()
    }
}