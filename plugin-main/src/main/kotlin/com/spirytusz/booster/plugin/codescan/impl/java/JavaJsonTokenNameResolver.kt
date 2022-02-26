package com.spirytusz.booster.plugin.codescan.impl.java

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiTypeElement
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.spirytusz.booster.plugin.codescan.resolve.JsonTokenNameResolver
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.log.MessageLogger

class JavaJsonTokenNameResolver(
    private val javaType: PsiTypeElement
) : JsonTokenNameResolver {
    override val resolvedJsonTokenName: JsonTokenName by lazy { resolveJsonTokenName() }

    private fun resolveJsonTokenName(): JsonTokenName {
        val primitiveJsonTokenName = tryGetPrimitiveJsonTokenName()
        if (primitiveJsonTokenName != null) {
            return primitiveJsonTokenName
        }

        val collectionJsonTokenName = tryGetCollectionJsonTokenName()
        if (collectionJsonTokenName != null) {
            return collectionJsonTokenName
        }

        val enumJsonTokenName = tryGetEnumJsonTokenName()
        if (enumJsonTokenName != null) {
            return enumJsonTokenName
        }

        return JsonTokenName.OBJECT
    }

    private fun tryGetPrimitiveJsonTokenName(): JsonTokenName? {
        return when (getRawType()) {
            "int", java.lang.Integer::class.java.canonicalName -> JsonTokenName.INT
            "long", java.lang.Long::class.java.canonicalName -> JsonTokenName.LONG
            "float", java.lang.Float::class.java.canonicalName -> JsonTokenName.FLOAT
            "double", java.lang.Double::class.java.canonicalName -> JsonTokenName.DOUBLE
            "boolean", java.lang.Boolean::class.java.canonicalName -> JsonTokenName.BOOLEAN
            java.lang.String::class.java.canonicalName -> JsonTokenName.STRING
            else -> {
                null
            }
        }
    }

    private fun tryGetCollectionJsonTokenName(): JsonTokenName? {
        return when (getRawType()) {
            java.util.List::class.java.canonicalName -> JsonTokenName.JAVA_LIST
            java.util.Set::class.java.canonicalName -> JsonTokenName.JAVA_SET
            java.util.Map::class.java.canonicalName -> JsonTokenName.JAVA_MAP
            else -> {
                javaType.type.superTypes.forEach { psiType ->
                    val text = psiType.internalCanonicalText
                    if (text.contains(java.util.List::class.java.canonicalName)) {
                        return JsonTokenName.JAVA_LIST
                    }
                    if (text.contains(java.util.Set::class.java.canonicalName)) {
                        return JsonTokenName.JAVA_SET
                    }
                    if (text.contains(java.util.Map::class.java.canonicalName)) {
                        return JsonTokenName.JAVA_MAP
                    }
                }
                return null
            }
        }
    }

    private fun tryGetEnumJsonTokenName(): JsonTokenName? {
        val psiType = javaType.type
        if (psiType !is PsiClassReferenceType) {
            return null
        }
        val declaration = psiType.reference.resolve() as? PsiClass
        return if (declaration?.isEnum == true) {
            JsonTokenName.ENUM
        } else {
            null
        }
    }

    private fun getRawType(): String {
        val typeText = javaType.type.internalCanonicalText
        val indexOfLBrace = typeText.indexOfFirst { it == '<' }
        return if (indexOfLBrace > 0) {
            typeText.substring(0, indexOfLBrace)
        } else {
            typeText
        }
    }
}