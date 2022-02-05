package com.spirytusz.booster.processor.base.data

/**
 * 这个字段定义在哪里？
 *
 * 1. PRIMARY_CONSTRUCTOR -> 主构造方法
 * 2. BODY                -> 类体
 * 3. SUPERS              -> 超类，以及接口
 */
enum class DeclarationScope {
    PRIMARY_CONSTRUCTOR,
    BODY,
    SUPERS
}