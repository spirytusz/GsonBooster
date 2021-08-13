# GsonBooster
GsonBooster是一个自动化生成Gson TypeAdapter的工具库，帮助你提升在使用Gson进行Json序列化和反序列化的时间性能。

灵感来自[Kson](https://github.com/aafanasev/kson)，对其进行改进，并拓展出新的功能。

## 如何使用

在project的build.gradle加入
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

在app的build.gradle加入
```
dependencies {
    implementation 'com.github.zkw012300:GsonBooster:Tag'
}
```

为需要生成TypeAdapter的类加上@Kson注解，使得Booster注解处理器能够识别并生成对应的TypeAdapter

```
@Booster
data class Foo(
    @SerializedName("foo_int")
    val intValue: Int = 0,
    @SerializedName("foo_list_int")
    val listIntValue: List<Int> = listOf()
)
```

加上@BoosterFactory，使得Kson注解处理器能够识别并生成TypeAdapterFactory

```
@BoosterFactory
object CustomTypeAdapterFactory {
    // BoosterCustomTypeAdapterFactory是Kson注解处理器自动生成的类
    fun get() = BoosterCustomTypeAdapterFactory()
}
```

为Gson实例添加自动生成的TypeAdapterFactory

```
val gson = GsonBuilder()
        .registerTypeAdapterFactory(CustomTypeAdapterFactory.get())
        .create()
```

## 功能
Kson支持以下场景：

1. 支持默认值，如果没有找到对应key，或者对应key的类型不正确，则使用默认值；
2. 支持可空变量；
3. 支持枚举；
4. 支持简单集合类（泛型为原始类型或者不带泛型的Object类型）的序列化、反序列化加速；

不仅如此，GsonBooster生成的TypeAdapter在反序列化时具有一定的容错性：

遇到value为期望以外的类型（比如需要一个NUMBER, 但是是BEGIN_OBJECT），则会**跳过**该值的解析，使用默认值。

生成的代码就像这样：

```
"foo_bean_double" -> {
     val peeked = reader.peek()
     if (peeked == JsonToken.NUMBER) {
          doubleValue = reader.nextDouble()
     } else if (peeked == JsonToken.NULL) {
          reader.nextNull()
     } else {
          reader.skipValue()
     }
}
```

## 限制

GsonBooster是在以下规则下运作的：


|  限制  | 是否可选  |
|  ---  | ---  |
| 集合类泛型不可空 | **必选** |
| 集合类必须声明为List或Set的顶级接口 | **必选** |
| 不使用kotlin关键词来命名变量 | **必选** |
| 类需要提供一个**无参**构造方法，和一个包含所有需要序列化&反序列化（**包括父类、接口的**）**不可变**字段的构造方法 | **必选** |
| 如果继承了类或实现了接口，父类或接口需要序列化&反序列化的字段需要体现在构造方法上 | **必选** |
| 避免使用类泛型 | 可选 |
| 集合类泛型不使用协变、逆变以及通配符 | 可选 |
| 避免使用嵌套集合类型、以及泛型嵌套类型 | 可选 |

