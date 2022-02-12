package com.spirytusz.booster.processor.scan.ksp.impl

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.spirytusz.booster.processor.base.data.type.JsonTokenName
import com.spirytusz.booster.processor.base.log.MessageLogger
import com.spirytusz.booster.processor.scan.ksp.extensions.findActualDeclaration
import com.spirytusz.booster.processor.scan.ksp.extensions.isChildTypeOf

class JsonTokenNameResolver(
    private val resolver: Resolver,
    private val logger: MessageLogger
) {
    fun resolve(ksType: KSType): JsonTokenName {
        return when {
            ksType.isAssignableFrom(resolver.builtIns.intType) -> JsonTokenName.INT
            ksType.isAssignableFrom(resolver.builtIns.longType) -> JsonTokenName.LONG
            ksType.isAssignableFrom(resolver.builtIns.floatType) -> JsonTokenName.FLOAT
            ksType.isAssignableFrom(resolver.builtIns.doubleType) -> JsonTokenName.DOUBLE
            ksType.isAssignableFrom(resolver.builtIns.stringType) -> JsonTokenName.STRING
            ksType.isAssignableFrom(resolver.builtIns.booleanType) -> JsonTokenName.BOOLEAN
            ksType.isList() -> JsonTokenName.LIST
            ksType.isJavaList() -> JsonTokenName.JAVA_LIST
            ksType.isSet() -> JsonTokenName.SET
            ksType.isJavaSet() -> JsonTokenName.JAVA_SET
            ksType.isMap() -> JsonTokenName.MAP
            ksType.isJavaMap() -> JsonTokenName.JAVA_MAP
            ksType.isEnum() -> JsonTokenName.ENUM
            else -> JsonTokenName.OBJECT
        }
    }

    private fun KSType.isList(): Boolean {
        val typeFqName = declaration.findActualDeclaration().qualifiedName?.asString().toString()
        return typeFqName.startsWith("kotlin") && this.isChildTypeOf(List::class.qualifiedName!!)
    }

    private fun KSType.isJavaList(): Boolean {
        val typeFqName = declaration.findActualDeclaration().qualifiedName?.asString().toString()
        return typeFqName.startsWith("java") && this.isChildTypeOf(List::class.qualifiedName!!)
    }

    private fun KSType.isSet(): Boolean {
        val typeFqName = declaration.findActualDeclaration().qualifiedName?.asString().toString()
        return typeFqName.startsWith("kotlin") && this.isChildTypeOf(Set::class.qualifiedName!!)
    }

    private fun KSType.isJavaSet(): Boolean {
        val typeFqName = declaration.findActualDeclaration().qualifiedName?.asString().toString()
        return typeFqName.startsWith("java") && this.isChildTypeOf(Set::class.qualifiedName!!)
    }

    private fun KSType.isMap(): Boolean {
        val typeFqName = declaration.findActualDeclaration().qualifiedName?.asString().toString()
        return typeFqName.startsWith("kotlin") && this.isChildTypeOf(Map::class.qualifiedName!!)
    }

    private fun KSType.isJavaMap(): Boolean {
        val typeFqName = declaration.findActualDeclaration().qualifiedName?.asString().toString()
        return typeFqName.startsWith("java") && this.isChildTypeOf(Map::class.qualifiedName!!)
    }

    private fun KSType.isEnum(): Boolean {
        return (declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS
    }
}