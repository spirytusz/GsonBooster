package com.spirytusz.booster.processor.strategy.declare

import com.spirytusz.booster.processor.data.type.KType
import com.squareup.kotlinpoet.PropertySpec

/**
 * 生成类型为[KType]对应TypeAdapter的变量声明语句的抽象.
 * 不同类型的变量都会派生出一个实现类, 负责指定类型的变量的TypeAdapter声明.
 *
 * 比如:
 * val foo: Foo = Foo()
 * kType为Foo, 这个类负责生成语句：
 * private val fooTypeAdapter by lazy { FooTypeAdapter() }
 *
 * 目前有以下多种[KType]:
 *
 * 1. [PrimitiveKType]  原始类型, 对应[PrimitiveAdapterDeclareStrategy], 不会生成TypeAdapter声明语句
 * 2. [ObjectKType]     不带泛型的Object类型, 对应[ObjectAdapterDeclareStrategy], 会生成TypeAdapter声明语句
 * 3. [CollectionKType] 泛型为[PrimitiveKType]、[ObjectKType]的集合类型, 对应[CollectionAdapterDeclareStrategy], 会生成TypeAdapter声明语句
 * 4. [EnumKType]       不带泛型的枚举类型, 对应[EnumTypeAdapterDeclareStrategy], 会生成TypeAdapter声明语句
 * 5. [BackoffKType]    除了以上类型的其他类型, 对应[BackoffAdapterDeclareStrategy], 会生成TypeAdapter声明语句
 *
 * 分发逻辑详见[AdapterDeclareStrategy]
 */
interface IAdapterDeclareStrategy {

    /**
     * 给定的[kType]来生成对应的TypeAdapter声明代码
     *
     * @param kType 变量类型
     *
     * @return 返回语句所代表的对象, 不需要生成就返回null
     */
    fun declare(kType: KType): PropertySpec?
}