package com.spirytusz.booster.processor.base.data

/**
 * 这个字段的的初始化方式？
 *
 * 1. NONE        -> 无
 * 2. HAS_DEFAULT -> 默认值
 * 3. DELEGATED   -> 委托
 */
enum class FieldInitializer {
    NONE,
    HAS_DEFAULT,
    DELEGATED
}