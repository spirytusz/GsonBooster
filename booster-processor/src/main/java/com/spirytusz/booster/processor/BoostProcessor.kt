package com.spirytusz.booster.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.spirytusz.booster.annotation.Boost
import com.spirytusz.booster.processor.base.BaseSymbolProcessor
import com.spirytusz.booster.processor.check.CodeCheckProcessor
import com.spirytusz.booster.processor.gen.BoosterCodegenProcessor
import com.spirytusz.booster.processor.scan.ClassScanProcessor
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

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

    @KotlinPoetKspPreview
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classScanners = ClassScanProcessor(resolver, environment).process()
        CodeCheckProcessor(environment).process(classScanners)
        BoosterCodegenProcessor(environment).process(classScanners)
        return emptyList()
    }
}