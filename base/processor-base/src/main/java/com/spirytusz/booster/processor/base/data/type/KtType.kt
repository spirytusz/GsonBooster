package com.spirytusz.booster.processor.base.data.type

/**
 * 代表一个Kotlin类型
 */
interface KtType {

    val rawType: String

    val nullable: Boolean

    /**
     * 可变性
     */
    val variance: KtVariance

    /**
     * 代表Gson的JsonToken
     */
    val jsonTokenName: JsonTokenName

    /**
     * 泛型
     */
    val generics: List<KtType>

    /**
     * 复制一个[KtType]
     */
    fun copy(
        nullable: Boolean = this.nullable,
        variance: KtVariance = this.variance
    ): KtType

    /**
     * 根据[predicate]搜索，返回一个集合
     */
    fun dfs(predicate: KtType.() -> Boolean): Set<KtType> {
        val result = if (predicate(this)) {
            setOf(this)
        } else {
            setOf()
        }
        return result + generics.map { it.dfs(predicate) }.flatten().toSet()
    }

    /**
     * 转换成可读的字符串，如:
     * kotlin.collections.List<kotlin.Int? INT>? LIST
     */
    fun toReadableString(): String = buildString {
        if (variance in listOf(KtVariance.IN, KtVariance.OUT)) {
            append(variance)
            append(" ")
        }
        append(rawType)
        if (generics.isNotEmpty()) {
            val genericsName = generics.map { it.toString() }.joinToString { it }
            append("<")
            append(genericsName)
            append(">")
        }
        if (nullable) {
            append("?")
        }
        append(" ")
        append(jsonTokenName)
    }
}