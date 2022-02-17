package com.spirytusz.booster.processor.scan.kapt

import com.spirytusz.booster.processor.base.data.BoosterClassKind
import com.spirytusz.booster.processor.base.log.MessageLogger
import kotlinx.metadata.Flag
import javax.lang.model.element.TypeElement

class KmClassKindResolver(
    private val belongingClass: TypeElement,
    private val kmClassCacheHolder: KmClassCacheHolder,
    private val logger: MessageLogger
) {

    private val kmClass by lazy {
        kmClassCacheHolder.get(belongingClass)
    }

    fun resolveClassKind(): BoosterClassKind {
        return when {
            Flag.Class.IS_CLASS(kmClass.flags) -> BoosterClassKind.CLASS
            Flag.Class.IS_ANNOTATION_CLASS(kmClass.flags) -> BoosterClassKind.ANNOTATION
            Flag.Class.IS_ENUM_ENTRY(kmClass.flags) -> BoosterClassKind.ENUM_ENTRY
            Flag.Class.IS_ENUM_CLASS(kmClass.flags) -> BoosterClassKind.ENUM_CLASS
            Flag.Class.IS_INTERFACE(kmClass.flags) -> BoosterClassKind.INTERFACE
            Flag.Class.IS_OBJECT(kmClass.flags) || Flag.Class.IS_COMPANION_OBJECT(kmClass.flags) -> BoosterClassKind.OBJECT
            else -> {
                logger.error("unexpected class kind on class $belongingClass", belongingClass)
                throw IllegalArgumentException("unexpected class kind on class $belongingClass")
            }
        }
    }
}