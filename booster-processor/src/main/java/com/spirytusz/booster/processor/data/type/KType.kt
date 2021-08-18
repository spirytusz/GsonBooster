package com.spirytusz.booster.processor.data.type

import com.spirytusz.booster.processor.helper.TypeHelper
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName

/**
 * Kotlin类型的抽象.
 *
 * 目前已有:
 * 1. [PrimitiveKType]  -> 原始类型, [Int], [Long], [Float], [Double], [Boolean], [String]
 * 2. [ObjectKType]     -> 不带泛型的Object类型
 * 3. [EnumKType]       -> 不带泛型的枚举类型
 * 4. [CollectionKType] -> 泛型为[PrimitiveKType] 或 [ObjectKType]的集合类型
 * 5. [BackoffKType]    -> 除以上类型的其他类型
 *
 * @param typeName 类型名
 */
abstract class KType(val typeName: TypeName) {

    /**
     * 这个类型对应的TypeAdapter的变量名
     */
    abstract val adapterFieldName: String

    /**
     * 这个类型对应的JsonToken
     */
    abstract val jsonTokenName: String

    companion object {

        /**
         * 根据[typeName]来构造对应的KType
         *
         * @param typeName 类型
         *
         * @return KType
         */
        fun makeKTypeByTypeName(typeName: TypeName): KType {
            return when {
                PrimitiveKType.isPrimitive(typeName) -> {
                    // 原始类型
                    PrimitiveKType(typeName as ClassName)
                }
                TypeHelper.isList(typeName) || TypeHelper.isSet(typeName) -> {
                    // 集合类型
                    val parameterizedTypeName = typeName as ParameterizedTypeName
                    val generic = parameterizedTypeName.typeArguments.first()
                    if (generic is ClassName) {
                        CollectionKType(
                            parameterizedTypeName,
                            parameterizedTypeName.rawType,
                            generic
                        )
                    } else {
                        BackoffKType(typeName)
                    }
                }
                TypeHelper.isEnum(typeName) && typeName is ClassName -> {
                    // 不带泛型的枚举类型
                    EnumKType(typeName)
                }
                typeName is ClassName -> {
                    // 不带泛型的Class<*>类型
                    ObjectKType(typeName)
                }
                else -> {
                    // 退避类型，这种类型托管给gson自带的TypeAdapter解析
                    BackoffKType(typeName)
                }
            }
        }
    }
}