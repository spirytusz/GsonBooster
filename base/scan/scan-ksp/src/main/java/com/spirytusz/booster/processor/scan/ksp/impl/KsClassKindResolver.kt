package com.spirytusz.booster.processor.scan.ksp.impl

import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.spirytusz.booster.processor.base.data.BoosterClassKind
import com.spirytusz.booster.processor.base.log.MessageLogger

class KsClassKindResolver(
    private val ksClass: KSClassDeclaration,
    private val logger: MessageLogger
) {

    fun resolveClassKind(): BoosterClassKind {
        return when (ksClass.classKind) {
            ClassKind.CLASS -> BoosterClassKind.CLASS
            ClassKind.ENUM_CLASS -> BoosterClassKind.ENUM_CLASS
            ClassKind.INTERFACE -> BoosterClassKind.INTERFACE
            ClassKind.ANNOTATION_CLASS -> BoosterClassKind.ANNOTATION
            ClassKind.ENUM_ENTRY -> BoosterClassKind.ENUM_ENTRY
            ClassKind.OBJECT -> BoosterClassKind.OBJECT
        }
    }
}