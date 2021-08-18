package com.spirytusz.booster.processor.strategy.read

import com.spirytusz.booster.processor.data.KField
import com.squareup.kotlinpoet.CodeBlock

/**
 * 反序列化时读取语句生成器的抽象.
 * 负责生成指定字段[KField]的反序列化逻辑语句.
 *
 * 比如:
 * val foo: Foo = Foo()
 *
 * kField为foo, 这个类负责生成语句:
 * foo = fooTypeAdapter.read(reader)
 *
 * 目前根据[KField.kType]的类型来派生出以下实现类:
 *
 * 1. [PrimitiveKType]  原始类型, 对应[PrimitiveFieldReadStrategy]
 * 2. [ObjectKType]     不带泛型的Object类型, 对应[ObjectFieldReadStrategy]
 * 3. [CollectionKType] 泛型为[PrimitiveKType]、[ObjectKType]的集合类型, 对应[CollectionFieldReadStrategy]
 * 4. [EnumKType]       不带泛型的枚举类型, 对应[EnumFieldReadStrategy]
 * 5. [BackoffKType]    除了以上类型的其他类型, 对应[ObjectFieldReadStrategy]
 *
 * 分发逻辑详见[FieldReadStrategy]
 */
interface IFieldReadStrategy {

    /**
     * 根据[kField]生成反序列化逻辑代码
     *
     * @param kField 字段
     *
     * @return 生成的反序列化逻辑代码
     */
    fun read(kField: KField): CodeBlock
}