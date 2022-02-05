package com.spirytusz.booster.processor.scan.kapt

import com.spirytusz.booster.processor.base.log.MessageLogger
import kotlinx.metadata.KmClass
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.TypeElement

class KmClassCacheHolder(private val logger: MessageLogger) {

    private val cache = mutableMapOf<String, KmClass>()

    fun get(typeElement: TypeElement): KmClass {
        val fqName = typeElement.qualifiedName.toString()
        return cache.getOrPut(fqName) {
            val kmClass = typeElement.asKmClass()
                ?: throw IllegalStateException("Unexpected metadata received for element $fqName")
            kmClass
        }
    }

    private fun TypeElement.asKmClass(): KmClass? {
        val metadataAnnotation = getAnnotation(Metadata::class.java) ?: run {
            logger.error("@Metadata annotation not found", this)
            return null
        }
        val header = KotlinClassHeader(
            kind = metadataAnnotation.kind,
            //bytecodeVersion = metadataAnnotation.bytecodeVersion,
            metadataVersion = metadataAnnotation.metadataVersion,
            data1 = metadataAnnotation.data1,
            data2 = metadataAnnotation.data2,
            extraInt = metadataAnnotation.extraInt,
            extraString = metadataAnnotation.extraString,
            packageName = metadataAnnotation.packageName
        )
        val kotlinClassMetadata = KotlinClassMetadata.read(header) ?: run {
            logger.error("parse KotlinClassMetadata return null", this)
            return null
        }
        kotlinClassMetadata as? KotlinClassMetadata.Class ?: run {
            logger.error("parse result is NOT KotlinClassMetadata.Class", this)
            return null
        }
        return kotlinClassMetadata.toKmClass()
    }
}