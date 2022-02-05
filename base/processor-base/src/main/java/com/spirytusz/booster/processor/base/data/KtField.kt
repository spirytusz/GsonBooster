package com.spirytusz.booster.processor.base.data

import com.spirytusz.booster.processor.base.data.type.KtType

/**
 * 代表一个变量
 */
interface KtField {

    /**
     * 是否可变
     */
    val isFinal: Boolean

    /**
     * 变量名
     */
    val fieldName: String

    /**
     * serialize names
     */
    val keys: List<String>

    /**
     * 类型
     */
    val ktType: KtType

    /**
     * 初始化器，是否有默认值，或者没有初始化器（包括delegate）
     */
    val initializer: FieldInitializer

    /**
     * 定义处，主构造器、类体或超类型中
     */
    val declarationScope: DeclarationScope

    val transient: Boolean

    /**
     * 复制一个[KtField]
     */
    fun copy(declarationScope: DeclarationScope = this.declarationScope): KtField

    /**
     * 转换成可读的字符串，如:
     * val listInt: kotlin.collections.List<kotlin.Int? INT>? LIST HAS_DEFAULT BODY ["list_int"]
     */
    fun toReadableString(): String = buildString {
        if (isFinal) {
            append("val ")
        } else {
            append("var ")
        }
        append(fieldName)
        append(" : ")
        append(ktType.toString())
        append(" ")
        append(initializer.toString())
        append(" ")
        append(declarationScope.toString())
        append(" keys: ")
        append(keys.toString())
    }
}