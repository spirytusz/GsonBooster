package com.spirytusz.booster.plugin.codescan.impl.kotlin

import com.spirytusz.booster.plugin.codescan.resolve.JsonTokenNameResolver
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import org.jetbrains.kotlin.idea.refactoring.fqName.fqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.supertypes

class KtJsonTokenNameResolver(
    private val kotlinType: KotlinType
) : JsonTokenNameResolver {

    override val resolvedJsonTokenName: JsonTokenName by lazy { resolveJsonTokenName() }

    private val kotlinCollectionJavaCollectionMapping = mapOf(
        JsonTokenName.LIST to JsonTokenName.JAVA_LIST,
        JsonTokenName.SET to JsonTokenName.JAVA_SET,
        JsonTokenName.MAP to JsonTokenName.JAVA_MAP
    )

    private fun resolveJsonTokenName(): JsonTokenName {
        val primitiveJsonTokenName = tryGetPrimitiveJsonTokenName(kotlinType)
        if (primitiveJsonTokenName != null) {
            return primitiveJsonTokenName
        }

        val collectionJsonTokenName = tryGetCollectionJsonTokenName(kotlinType)
        if (collectionJsonTokenName != null) {
            return if (kotlinType.fqName.toString().startsWith("java")) {
                kotlinCollectionJavaCollectionMapping[collectionJsonTokenName]!!
            } else {
                collectionJsonTokenName
            }
        }

        val enumJsonTokenName = tryGetEnumJsonTokenName(kotlinType)
        if (enumJsonTokenName != null) {
            return enumJsonTokenName
        }

        return JsonTokenName.OBJECT
    }

    private fun tryGetPrimitiveJsonTokenName(kotlinType: KotlinType): JsonTokenName? {
        return when (kotlinType.fqName.toString()) {
            Int::class.qualifiedName -> JsonTokenName.INT
            Long::class.qualifiedName -> JsonTokenName.LONG
            Float::class.qualifiedName -> JsonTokenName.FLOAT
            Double::class.qualifiedName -> JsonTokenName.DOUBLE
            Boolean::class.qualifiedName -> JsonTokenName.BOOLEAN
            String::class.qualifiedName -> JsonTokenName.STRING
            else -> {
                null
            }
        }
    }

    private fun tryGetCollectionJsonTokenName(kotlinType: KotlinType): JsonTokenName? {
        when (kotlinType.fqName.toString()) {
            List::class.qualifiedName -> return JsonTokenName.LIST
            Set::class.qualifiedName -> return JsonTokenName.SET
            Map::class.qualifiedName -> return JsonTokenName.MAP
            else -> {
                kotlinType.supertypes().forEach { supertype ->
                    val jsonTokenName = tryGetCollectionJsonTokenName(supertype)
                    if (jsonTokenName != null) {
                        return jsonTokenName
                    }
                }
                return null
            }
        }
    }

    private fun tryGetEnumJsonTokenName(kotlinType: KotlinType): JsonTokenName? {
        return if (kotlinType.isEnum()) {
            JsonTokenName.ENUM
        } else {
            null
        }
    }
}