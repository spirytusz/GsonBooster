# GsonBooster
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.spirytusz/booster-annotation/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.spirytusz/booster-annotation)

GsonBooster是一个自动化生成Gson TypeAdapter的工具库。

思路由[Kson](https://github.com/aafanasev/kson)提供，GsonBooster是对Kson的改进和拓展。

## 接入
```
implementation "com.spirytusz:booster-annotation:1.1.0"
kapt "com.spirytusz:booster-processor:1.1.0"
```

## 使用

为需要生成TypeAdapter的类加上`@Boost`注解

```
@Boost
data class Foo(
    @SerializedName("foo_int")
    val intValue: Int = 0,
    @SerializedName("foo_list_int")
    val listIntValue: List<Int> = listOf()
)
```

为Gson实例添加自动生成的`BoosterTypeAdapterFactory `

```
val gson = GsonBuilder()
        .registerTypeAdapterFactory(BoosterTypeAdapterFactory())
        .create()
```

## 功能
GsonBooster支持以下场景：

1. 支持默认值；
2. 支持可空变量；
3. 支持枚举；
4. 支持简单集合类（泛型为原始类型或者不带泛型的Object类型）的序列化、反序列化加速；
5. 一定的容错性，类型不对会使用默认值。（详见[测试报告](booster-test/TestingReport.md)）

## 约定

GsonBooster简单用法的背后，需要接入方的支持：

1. 集合类泛型不能是可空的；
2. Json Array必须声明为List或Set；
3. 不使用Kotlin关键词为变量命名；
4. 类需要提供一个无参构造方法，或者构造方法的所有参数必须有默认值；
5. 如果继承了类或实现了接口，父类和接口需要序列化 or 反序列化的字段需要体现在构造方法上；

## Benchmark

* OS: Mac OS X
* JVM: JDK 1.8.0_302, OpenJDK 64-Bit Server VM, 25.302-b08
* CPU: Intel Core i7
* Core: 4

```
Benchmark                         Mode  Cnt      Score      Error  Units
Benchmarks.generatedTypeAdapter   avgt    6  31265.404 ± 8870.324  ns/op
Benchmarks.reflectiveTypeAdapter  avgt    6  40666.818 ± 2422.591  ns/op
```

## License
```
MIT License

Copyright (c) 2021 ZSpirytus

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
