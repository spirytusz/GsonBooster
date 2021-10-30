package com.spirytusz.booster.processor

import com.google.auto.service.AutoService
import com.spirytusz.booster.annotation.Boost
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * [Boost]注解处理器
 *
 * 负责：
 *     1. 扫描获取所有被[Boost]注解的类
 *     2. 生成TypeAdapter
 *     3. 生成TypeAdapterFactory
 */
@AutoService(Processor::class)
class BoostProcessor : AbstractProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        return false
    }
}