package com.spirytusz.booster.processor.strategy.write

import com.spirytusz.booster.processor.data.KField
import com.squareup.kotlinpoet.CodeBlock

/**
 * 序列化时读取语句生成器的抽象.
 * 负责生成指定字段[KField]的序列化逻辑语句.
 *
 * 比如:
 * val foo: Foo = Foo()
 *
 * kField为foo, 这个类负责生成语句:
 * fooTypeAdapter.write(writer, foo)
 *
 * 目前根据[KField.kType]的类型来派生出以下实现类:
 *
 * 1. [PrimitiveKType]  原始类型, 对应[PrimitiveFieldWriteStrategy]
 * 2. [ObjectKType]     不带泛型的Object类型, 对应[ObjectTypeFieldWriteStrategy]
 * 3. [CollectionKType] 泛型为[PrimitiveKType]、[ObjectKType]的集合类型, 对应[CollectionFieldWriteStrategy]
 * 4. [EnumKType]       不带泛型的枚举类型, 对应[EnumFieldWriteStrategy]
 * 5. [BackoffKType]    除了以上类型的其他类型, 对应[ObjectTypeFieldWriteStrategy]
 *
 * 分发逻辑详见[FieldWriteStrategy]
 */
interface IFieldWriteStrategy {

    /**
     * 根据[kField]生成序列化逻辑代码
     *
     * @param kField 字段
     *
     * @return 生成的序列化逻辑代码
     */
    fun write(kField: KField): CodeBlock
}